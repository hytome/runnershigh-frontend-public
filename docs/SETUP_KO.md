# 로컬 실행 가이드

이 문서는 프론트 개발자가 레포를 처음 받은 뒤 Android Studio에서 바로 개발할 수 있게 만드는 절차입니다.

## 1. 레포 클론

```powershell
git clone https://github.com/hytome/runnershigh-frontend-public.git
cd runnershigh-frontend-public
```

이미 로컬에 받은 상태라면 해당 폴더만 Android Studio에서 열면 됩니다.

## 2. Android Studio로 열기

Android Studio에서 `Open`을 누르고 `runnershigh-frontend-public` 폴더를 선택합니다.

열면 Gradle Sync가 실행됩니다. 최초 실행 시 Gradle과 Android 의존성을 다운로드하므로 시간이 조금 걸릴 수 있습니다.

## 3. SDK 설정

`local.properties`는 각자 PC의 Android SDK 경로가 들어가는 파일이라 Git에 올리지 않습니다.

Android Studio로 열면 보통 자동 생성됩니다. 직접 만들어야 한다면 아래처럼 작성합니다.

```properties
sdk.dir=C\:\\Users\\본인계정\\AppData\\Local\\Android\\Sdk
```

## 4. 기본 빌드

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :wear:assembleDebug
```

`BUILD SUCCESSFUL`이 나오면 기본 개발 환경은 준비된 상태입니다.

## 5. 실제 서비스 연동

지도, Firebase 로그인, 실제 백엔드 호출을 확인하려면 다음 파일을 로컬에 만들어야 합니다.

- `secrets.properties`
- `app/google-services.json`

자세한 내용은 [민감정보 설정](SECRETS_KO.md)을 참고하세요.

