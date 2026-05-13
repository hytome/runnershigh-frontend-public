# 공개 레포 업로드 체크리스트

공개 GitHub 레포에 push하기 전에 확인하는 문서입니다.

## 코드 포함 범위

- `app` 메인 Android 앱 포함
- `wear` Wear OS 앱 포함
- `gpxapi` 코스/GPX 참고 코드 포함
- Gradle wrapper 포함
- 리소스 이미지, XML, 폰트 포함

## 공개하면 안 되는 파일 제외

- `google-services.json` 제외
- `secrets.properties` 제외
- `local.properties` 제외
- `build/`, `.gradle/`, `release/` 제외
- APK/AAB/ZIP 제외
- keystore 제외

## 민감정보 검색

```powershell
rg -n "(AIza|secret|client_secret|Bearer|Authorization|run\.app|firebasestorage|NCP|NAVER)" .
```

검색 결과에 실제 키나 운영 URL이 있으면 push하지 않습니다.

## 빌드 확인

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :wear:assembleDebug
```

## 현재 공개 레포에서 의도적으로 바뀐 것

- Cloud Run 실제 URL은 `https://example.com/...`으로 교체
- Firebase 설정은 `app/google-services.example.json`만 제공
- Naver Maps/NCP 키는 `secrets.properties.example`만 제공
- `gpxapi` 안에 주석으로 남아 있던 키도 placeholder로 교체

이 상태는 공개용으로 안전하지만, 실제 앱처럼 로그인/회원가입을 확인하려면 개발자가 `ApiEndpoints.kt`의 placeholder URL을 실제 팀 백엔드 URL로 바꿔야 합니다.

