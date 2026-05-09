# 민감정보 설정

이 레포는 공개 레포이기 때문에 실제 키와 운영 서버 주소가 들어 있지 않습니다.

실제 앱 기능을 로컬에서 확인하려면 각 개발자가 본인 환경에만 secret 파일을 만들어야 합니다.

## Git에 올리면 안 되는 파일

- `secrets.properties`
- `app/google-services.json`
- `local.properties`
- keystore 파일
- APK/AAB/ZIP 같은 빌드 산출물

위 파일들은 `.gitignore`에 포함되어 있습니다.

## Naver Maps / NCP 키

루트에 있는 예시 파일을 복사합니다.

```powershell
Copy-Item secrets.properties.example secrets.properties
```

그다음 값을 채웁니다.

```properties
NAVER_MAPS_CLIENT_ID=실제-client-id
NAVER_MAPS_KEY_ID=실제-ncp-key-id
NAVER_MAPS_KEY=실제-ncp-key
NAVER_MAPS_BASE_URL=https://maps.apigw.ntruss.com/
```

이 값들은 `app/build.gradle.kts`에서 읽혀 `BuildConfig`와 AndroidManifest placeholder로 들어갑니다.

## Firebase 설정

샘플 파일을 복사합니다.

```powershell
Copy-Item app/google-services.example.json app/google-services.json
```

Firebase 콘솔에서 받은 실제 `google-services.json` 내용으로 교체합니다.

`app/google-services.json`이 있을 때만 Google Services Gradle plugin이 적용됩니다. 그래서 공개 레포를 처음 클론한 상태에서도 Android Studio로 열고 기본 빌드할 수 있습니다.

## 백엔드 API 주소

공개 레포의 API 주소는 실제 Cloud Run URL이 아니라 placeholder입니다.

수정 위치:

```text
app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt
```

실제 서버와 연동하려면 `https://example.com/...` 값을 운영 또는 개발 서버 URL로 바꿔야 합니다.

## 이미 노출된 키가 의심될 때

공개 push 전에 아래처럼 검색합니다.

```powershell
rg -n "(AIza|secret|client_secret|Bearer|Authorization|run\.app|firebasestorage|NCP|NAVER)" .
```

실제 값이 보이면 커밋하지 말고 placeholder나 로컬 secret 파일로 빼야 합니다.

