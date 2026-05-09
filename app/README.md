# Runners High 안드로이드 프런트엔드 가이드

이 문서는 프런트엔드(Android/Kotlin) 모듈에서 현재 작성된 모든 Kotlin 파일과 디렉터리의 역할을 백엔드 개발자도 이해할 수 있도록 상세히 설명합니다. Compose UI와 Retrofit 네트워크 계층이 어떻게 이어지는지, 화면 간 데이터 흐름과 보조 유틸리티를 한 번에 파악할 수 있습니다.

## 프로젝트 구조 개요
```
app/
├─ src/main/java/com/example/runnershigh/
│  ├─ data/        # 외부 API 연동, Health Connect, Repository 계층
│  ├─ domain/      # UI에서 공통으로 쓰는 러닝 상태/통계 모델
│  ├─ navigation/  # 화면 전환 그래프
│  ├─ ui/          # Compose 화면, ViewModel, 테마, 지도 도우미
│  └─ util/        # Pace 변환 등 공통 함수
└─ src/main/res    # Compose 외 리소스(아이콘, 문자열 등)
```

## data 디렉터리
### remote
- **ApiEndpoints.kt**: 백엔드 Cloud Run 엔드포인트 상수 모음. 로그인/회원가입, 러닝 세션 시작·완료, 활동 통계, 배지/미션, 헬스 데이터 업로드까지 모든 REST URL을 한곳에서 정의합니다. 코멘트에 각 API 목적이 한글로 정리되어 있어 호출 의도를 쉽게 파악할 수 있습니다.
- **ApiClient.kt**: Retrofit + OkHttp 클라이언트를 한 번만 생성하는 싱글톤. Moshi 컨버터와 HTTP 로깅 인터셉터를 붙이고, `RunningApi`, `AuthApi`, `UserService`, `ActivityApi`, `AnalysisApi`, `HealthApi` 인터페이스 인스턴스를 제공합니다.

#### remote/api (분석·활동 도메인 전용 인터페이스)
- **ActivityApi.kt**: 활동(통계) 전용 API 집합. 월간/연간 요약, 최근 활동, 평균 심박 구간, 마라톤 피드백, 목표 달성도, 컨디션 레벨, 업적 조회 등을 GET으로 제공합니다. 모든 호출은 `userId`와 기간 파라미터를 받아 `ActivityStatsResponse` 등 DTO를 반환합니다.
- **AnalysisApi.kt**: 주간 컨디션, 부상 위험, 페이스 드롭, HRV, 맞춤 피드백 같은 분석 엔드포인트를 정의합니다. `userId` 또는 `sessionId`를 쿼리로 받아 각각의 분석 결과 DTO를 반환합니다.

#### remote/dto (요청/응답 DTO & Retrofit 인터페이스)
- **RunningApi.kt**: 러닝 세션 시작(`startSession`), GPS 포인트 업로드, 목표 대비 비교 조회, 세션 완료(`finishSession`), 결과 조회, 피드백 생성/조회까지 모든 러닝 흐름을 담당하는 Retrofit 인터페이스.
- **AuthApi.kt**: 회원가입/로그인, 이메일·닉네임 중복 체크, 신체정보·목적·경험 업데이트, 레벨 조회/업데이트, 프로필·컨디션 조회를 묶은 인증/프로필 인터페이스.
- **HealthApi.kt**: 헬스 데이터 업로드 전용 POST 엔드포인트.
- **UserService.kt**: 홈 대시보드/오늘의 플랜 조회, 배지 리스트와 미션, 컨디션 상세 등을 조회하는 서브 서비스 인터페이스.
- **요청 DTO**: `StartSessionRequest`, `GpsPointRequest`, `FinishSessionRequest`, `RunningFeedbackRequest`, `BodyUpdateRequest`, `UpdatePurposeRequest`, `UpdateExperienceRequest`, `AcquireBadgeRequest`, `UpdateExperienceRequest`, `UserIdRequest`, `LoginRequest`, `SignupRequest` 등. API 사양에 맞춰 userId·sessionId, 거리/시간/페이스, 피드백 점수·부상 부위, 사용자 신체/목적/경험 등의 필드를 담습니다.
- **응답 DTO**: `StartSessionResponse`, `GpsPointResponse`, `FinishSessionResponse`, `SessionResultResponse`, `RunningCompareResponse`, `RunningFeedbackResponse`, `SubmittedFeedback`, `HomeDashboardResponse`, `ActivityDto`, `AnalysisDto`, `Badge`, `Mission`, `HeartRateData`, `UserCondition`, `UserLevel`, `BasicResponse`, `EmailCheckResponse`, `UsernameCheckResponse`, `LoginResponse`, `SignupResponse` 등. 러닝 결과, 획득 배지/경험치, 활동 요약, 컨디션·레벨, 계정 중복 여부 등을 표현합니다.

### health
- **HealthConnectManager.kt**: Android Health Connect를 통해 오늘의 걸음 수·소모 칼로리·이동 거리, 당일 심박수 리스트, 최근 수면 세션과 단계 요약을 읽어오는 헬퍼. 필요한 읽기 권한 세트를 미리 정의하고, `HealthConnectClient`를 lazy 생성합니다.

### repository
- **RunningRepository.kt**: `RunningApi` 호출을 래핑하여 `Result`로 반환. 세션 시작/종료, GPS 업로드, 목표 대비 비교, 결과 조회, 피드백 생성/조회 로직을 캡슐화하며, 서버 응답에 따라 획득 배지·경험치를 로컬에 저장해 후속 조회 응답에 병합합니다.
- **AuthRepository.kt**: 회원가입·로그인 및 이메일/닉네임 중복 확인, 신체정보/목적/경험 업데이트를 담당. 네트워크/비즈니스 에러를 명시적으로 구분하는 `SignupResult`, `LoginResult`, `EmailCheckResult`, `UsernameCheckResult`, `BodyUpdateResult` sealed class를 통해 UI가 상태를 직관적으로 처리할 수 있게 합니다.

## domain 디렉터리
- **RunningLocationState.kt**: 실시간 위치 추적 상태(경로 좌표 리스트, 누적 거리·고도, 추적 여부)를 보관하는 데이터 클래스.
- **RunningPlanGoal.kt**: 목표 거리/페이스, 플랜 제목을 담아 홈 대시보드의 플랜과 러닝 비교 API에 공유.
- **RunningStats.kt / RunningStatsMapper.kt**: 러닝 종료 시 화면에서 쓰는 거리·시간·칼로리·평균 심박·고도 상승·케이던스·페이스 정보를 관리하고, 서버 응답(`SessionResultResponse`)을 `RunningStats`로 변환합니다.
- **RunningFeedback.kt**: 코스 만족도, 난이도, 아픈 부위, 한줄 평을 담아 서버 피드백 요청에 사용.
- **DateTimeLabelUtil.kt**: 러닝 날짜/시간을 화면에 맞는 라벨로 포맷팅하는 유틸리티.

## ui 디렉터리
### ViewModel
- **AuthViewModel.kt**: 회원가입·로그인·프로필 입력 상태를 `StateFlow`로 관리. `AuthRepository`를 사용해 회원가입 후 프로필 저장, 중복 체크, 로그인 완료 시 userUuid 보관 등 UI 이벤트를 처리합니다.
- **RunningViewModel.kt**: 러닝 세션의 시작/종료, GPS 업로드, 결과/비교 조회, 피드백 제출, 위치 추적 상태 업데이트를 담당. `RunningRepository`를 통해 네트워크를 호출하고 `RunningLocationState`, `RunningPlanGoal`, `SessionResultResponse` 등을 흐름으로 노출합니다.

### screen (개별 Compose 화면)
- **MainScreen.kt**: 앱 진입 랜딩. 로그인/회원가입으로 이동 버튼 제공.
- **LoginScreen.kt / ForgotPasswordScreen.kt**: 이메일/비밀번호 입력과 비밀번호 재설정 진입 UI. 로그인 성공 시 러닝 메인으로 이동합니다.
- **RegisterScreen.kt / UserInfoScreen.kt / GoalSelectionScreen.kt / ExperienceScreen.kt / ThankYouScreen.kt**: 온보딩 플로우. 신체 정보 입력 → 목표 선택 → 경험 입력 → 회원가입 요청 → 완료 화면 순서로 동작하며 `AuthViewModel` 상태를 바인딩합니다.
- **RunningScreen.kt**: 하단 탭이 있는 러닝 메인. 홈 대시보드에서 오늘의 플랜을 불러와 목표를 설정하고, Start 버튼으로 세션을 시작해 카운트다운→실시간 러닝 화면으로 전환합니다. 러닝 종료 시 결과 조회 후 `RunningResultScreen`으로 이동합니다.
- **CountdownScreen.kt**: 러닝 시작 전 3초 카운트다운 UI.
- **ActiveRunningScreen.kt / RunningStatsOverlayScreen.kt**: 달리는 중 실시간 거리·페이스·경로를 보여주고 정지/완료 이벤트를 전달합니다.
- **RunningResultScreen.kt**: 세션 결과, 목표 대비 성과, 획득 배지/경험치 표시 및 피드백 화면 이동 버튼 제공.
- **RunningFeedbackScreen.kt**: 세션에 대한 만족도/난이도/통증 부위/코멘트 입력 후 서버 제출. 이미 제출된 피드백이 있으면 불러와서 보여줍니다.
- **ActiveScreen.kt / ConditionDetailScreen.kt**: 활동(통계) 메인과 컨디션 상세. 서버에서 받은 분석/활동 데이터를 카드/그래프로 렌더링하도록 구성되어 있습니다.
- **LvScreen.kt, LevelActivity.kt, BadgeActivity.kt, AcquiredBadgeAdapter.kt, LockedBadgeAdapter.kt**: 레벨/배지 관련 화면과 RecyclerView 어댑터. 획득/미획득 배지 리스트를 구분 렌더링합니다.
- **RunningMapSection.kt, NaverMapTestScreen.kt, MapViewUtils.kt**: 네이버 지도 SDK를 이용한 경로/마커/폴리라인 렌더링과 테스트 화면, 지도 뷰 초기화 유틸리티.

### theme
- **Color.kt, Type.kt, Theme.kt**: 앱 전역 색상/폰트/Material3 테마 설정.

## navigation 디렉터리
- **NavGraph.kt**: Compose `NavHost`로 모든 화면 경로를 정의. `RunningViewModel`, `AuthViewModel`을 액티비티 범위에서 공유하고, 로그인→러닝, 러닝→결과/피드백, 활동/레벨/지도 테스트 등의 이동 흐름을 설정합니다.

## util 디렉터리
- **PaceUtils.kt**: "분'초" 형식 페이스 문자열을 초 단위로 파싱하거나, 초 단위를 화면 표기용 문자열로 변환하는 함수 제공.

## MainActivity (엔트리 포인트)
- **MainActivity.kt**: Firebase 인증과 구글 로그인 클라이언트 초기화 후 `AppNavGraph`를 Compose로 그려 앱을 시작합니다. 구글 로그인 결과를 받아 FirebaseAuth에 연동하도록 `ActivityResultLauncher`를 등록합니다.

## 데이터 흐름 요약
1. **메인 진입** → `MainActivity`가 `AppNavGraph`를 띄워 `MainScreen` 렌더링.
2. **회원가입/로그인** → `AuthViewModel`이 `AuthRepository`를 통해 Auth API 호출, 성공 시 `userUuid`를 저장.
3. **홈/러닝** → `RunningScreen`이 홈 대시보드(`UserService`)에서 오늘의 플랜을 가져와 `RunningViewModel`에 목표를 설정. Start 시 `RunningRepository.startSession` 호출과 위치 추적이 시작됩니다.
4. **러닝 중** → `RunningViewModel`이 GPS 좌표 누적 및 필요 시 서버 업로드, 목표 대비 비교 호출을 수행. UI는 `ActiveRunningScreen`/지도 섹션을 통해 경로와 통계를 표시합니다.
5. **러닝 종료** → `finishSession`으로 결과를 서버에 전송하고, `getRunningResult`로 상세 기록·배지·경험치를 받아 `RunningResultScreen`에 표시. 이어서 `RunningFeedbackScreen`에서 세션 피드백을 제출합니다.
6. **활동/분석** → `ActiveScreen` 및 컨디션 상세 화면이 `ActivityApi`/`AnalysisApi` 데이터를 표시하여 장기 활동 통계를 제공합니다.

이 README는 프런트엔드 코드가 어떤 API를 호출하고 어떤 데이터 모델을 오가는지, 그리고 각 화면이 어떤 책임을 가지는지를 빠르게 파악하기 위한 참고 자료입니다.
