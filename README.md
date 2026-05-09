# RunnersHigh Android Frontend

러너스하이 Android 프론트엔드 공개 레포입니다.

이 레포는 프론트 개발자가 Android Studio에서 바로 열어 앱 화면, 러닝 플로우, 코스 크리에이터, Wear OS 연동 코드를 확인하고 개발할 수 있도록 정리한 버전입니다. 공개 레포이기 때문에 Firebase, Naver Maps, Cloud Run 같은 실제 키와 엔드포인트는 placeholder로 교체되어 있습니다.

## 먼저 보면 좋은 문서

- [로컬 실행 가이드](docs/SETUP_KO.md): 처음 클론한 뒤 Android Studio에서 여는 방법
- [문서 목차](docs/README_KO.md): 문서 전체 목록
- [프로젝트 구조](docs/PROJECT_STRUCTURE_KO.md): `app`, `wear`, `gpxapi`가 각각 무엇인지
- [기능별 코드 지도](docs/FEATURE_MAP_KO.md): 로그인, 러닝, 코스, 활동, 배지 기능이 어디에 있는지
- [민감정보 설정](docs/SECRETS_KO.md): 공개 레포에서 빠진 키를 로컬에 넣는 방법
- [코스/GPX 가이드](docs/COURSE_GPX_KO.md): 코스 크리에이터와 GPX 관련 코드 흐름
- [프론트 개발 협업 규칙](docs/CONTRIBUTING_KO.md): 브랜치, 커밋, 확인 절차

## 포함된 것

- `app`: 메인 Android 휴대폰 앱
- `wear`: Wear OS 심박수 수집/연동 앱
- `gpxapi`: 코스 크리에이터와 GPX 기능을 만들 때 사용된 참고 코드
- Gradle wrapper, version catalog, Android 빌드 설정
- Compose 화면, ViewModel, Repository, DTO, 지도/헬스/Wear 연동 코드

## 포함하지 않은 것

- Firebase 실제 설정 파일 `google-services.json`
- Naver Maps / NCP / Naver Search 실제 키
- 실제 Cloud Run 백엔드 URL
- Cloud Functions 백엔드 코드
- APK/AAB/ZIP, keystore, build/release 산출물

## 빠른 시작

```powershell
git clone https://github.com/hytome/runnershigh-frontend-public.git
cd runnershigh-frontend-public
```

Android Studio에서 이 폴더를 열면 됩니다.

키 없이도 프로젝트 구조 확인과 기본 빌드는 가능하게 정리되어 있습니다. 실제 지도, 로그인, 서버 연동까지 확인하려면 [민감정보 설정](docs/SECRETS_KO.md)을 따라 로컬 파일을 채워주세요.

## 기본 빌드 확인

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :wear:assembleDebug
```

## 개발할 때 자주 보는 위치

- 앱 진입점: `app/src/main/java/com/example/runnershigh/MainActivity.kt`
- 화면 이동: `app/src/main/java/com/example/runnershigh/navigation/NavGraph.kt`
- 러닝 상태: `app/src/main/java/com/example/runnershigh/ui/RunningViewModel.kt`
- 인증/회원가입 상태: `app/src/main/java/com/example/runnershigh/ui/AuthViewModel.kt`
- 백엔드 API 주소: `app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt`
- 코스 저장/GPX 생성: `app/src/main/java/com/example/runnershigh/data/repository/RunningRepository.kt`
- GPX 참고 코드: `gpxapi/myapplication/GpxManager.kt`
