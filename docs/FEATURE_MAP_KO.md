# 기능별 코드 지도

프론트 개발자가 “이 기능은 어디를 보면 되지?”라고 생각할 때 보는 문서입니다.

## 로그인과 회원가입

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/AuthViewModel.kt`
- `app/src/main/java/com/example/runnershigh/data/repository/AuthRepository.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/dto/AuthApi.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/LoginScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/RegisterScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/UserInfoScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/GoalSelectionScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/ExperienceScreen.kt`

흐름:

1. 사용자가 화면에 입력합니다.
2. `AuthViewModel`이 입력 상태와 검증 상태를 관리합니다.
3. `AuthRepository`가 Retrofit API를 호출합니다.
4. 성공하면 `userUuid`를 저장하고 러닝 화면으로 이동합니다.

## 러닝 시작과 기록

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/RunningViewModel.kt`
- `app/src/main/java/com/example/runnershigh/data/repository/RunningRepository.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/dto/RunningApi.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/RunningScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/CountdownScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/ActiveRunningScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/RunningStatsOverlayScreen.kt`

핵심 상태:

- 현재 러닝 세션
- GPS 좌표 목록
- 거리, 페이스, 시간
- 목표 대비 비교 결과
- 러닝 종료 후 결과 데이터

## 러닝 결과와 피드백

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/screen/RunningResultScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/RunningFeedbackScreen.kt`
- `app/src/main/java/com/example/runnershigh/domain/model/RunningStats.kt`
- `app/src/main/java/com/example/runnershigh/domain/model/RunningFeedback.kt`
- `app/src/main/java/com/example/runnershigh/domain/model/RunningStatsMapper.kt`

러닝 종료 후 서버 결과를 받아 화면에 보여주고, 사용자가 코스 만족도와 통증 부위 등을 제출할 수 있습니다.

## 코스 크리에이터

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/screen/course/RunningCourseScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/course/MyCourseScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/course/RegisterCourseScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/course/AddCourseScreen.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/dto/RunningCourseDto.kt`
- `app/src/main/java/com/example/runnershigh/data/repository/RunningRepository.kt`
- `app/src/main/java/com/example/runnershigh/util/GpxManager.kt`
- `gpxapi/myapplication/GpxManager.kt`

현재 메인 앱에서는 러닝 기록의 GPS 포인트를 기반으로 GPX를 만들고 코스로 저장합니다.

## 활동, 컨디션, 부상 분석

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/screen/active/ActiveScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/active/ActivityViewModel.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/active/ConditionDetailScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/active/InjuryDetailScreen.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/api/ActivityApi.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/api/AnalysisApi.kt`
- `app/src/main/java/com/example/runnershigh/data/repository/ActivityRepository.kt`

활동 통계, 컨디션 점수, 부상 관련 분석 화면이 이 영역에 모여 있습니다.

## 레벨과 배지

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/screen/level/LvScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/level/LevelActivity.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/level/BadgeActivity.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/level/AcquiredBadgeAdapter.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/level/LockedBadgeAdapter.kt`

Compose 화면과 기존 Android View/RecyclerView 코드가 함께 남아 있습니다.

## 지도와 위치

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/map/RunningMapSection.kt`
- `app/src/main/java/com/example/runnershigh/ui/map/NaverMapTestScreen.kt`
- `app/src/main/java/com/example/runnershigh/ui/map/MapViewUtils.kt`
- `app/src/main/java/com/example/runnershigh/data/repository/NaverGeocodeRepository.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/api/NaverGeocodeApi.kt`

Naver Maps SDK와 NCP 지도 API를 사용합니다. 실제 키는 `secrets.properties`에 넣어야 합니다.

## Wear OS 심박수

주요 파일:

- `app/src/main/java/com/example/runnershigh/data/wear/WearHeartRateManager.kt`
- `app/src/main/java/com/example/runnershigh/data/heartrate/WearDataLayerHeartRateSource.kt`
- `wear/src/main/java/com/example/runnershigh/wear/HeartRateForegroundService.kt`
- `wear/src/main/java/com/example/runnershigh/wear/HeartRateCommandListenerService.kt`
- `wear/src/main/java/com/example/runnershigh/wear/WearHeartRateContract.kt`

휴대폰 앱이 Wear 쪽에 심박수 수집 명령을 보내고, Wear가 수집한 심박수를 다시 전달하는 구조입니다.

