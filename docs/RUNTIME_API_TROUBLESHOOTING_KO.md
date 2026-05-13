# 로그인 405 / 회원가입 404 문제 해결

공개 레포를 그대로 빌드했을 때 로그인에서 `405`, 회원가입 마지막 단계에서 `404`가 난다면 가장 먼저 API 주소 설정을 확인해야 합니다.

## 결론

이 공개 레포에는 실제 Cloud Run / Firebase Functions URL이 들어 있지 않습니다.

공개 레포이기 때문에 아래 파일의 실제 운영 URL을 `https://example.com/...` placeholder로 바꿔두었습니다.

```text
app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt
```

따라서 이 파일을 그대로 빌드하면 앱이 운영 백엔드가 아니라 `example.com`으로 요청을 보냅니다.

## 왜 405가 나오나요?

로그인 코드는 다음처럼 `POST` 요청을 보냅니다.

```kotlin
@POST(ApiEndpoints.LOGIN_API)
suspend fun login(...)
```

그런데 공개 레포의 `LOGIN_API`는 기본적으로 이런 값입니다.

```kotlin
const val LOGIN_API = "https://example.com/login-api"
```

`example.com`은 우리 백엔드가 아닙니다. 그래서 `POST` 요청을 받았을 때 `405 Method Not Allowed`가 날 수 있습니다.

## 왜 회원가입은 404가 나오나요?

회원가입도 같은 이유입니다.

```kotlin
const val SIGNUP_API = "https://example.com/signup-api"
```

`example.com/signup-api`라는 실제 API가 없으므로 `404 Not Found`가 날 수 있습니다.

## Firebase 계정이 있는데도 왜 안 되나요?

Firebase에 계정이 존재하는 것과 앱이 우리 백엔드 API를 정확히 호출하는 것은 별개입니다.

이 앱의 로그인/회원가입 흐름은 단순히 Firebase SDK만 호출하는 구조가 아니라, `AuthApi`, `AuthRepository`를 통해 백엔드 API도 호출합니다.

그래서 `google-services.json`만 넣어도 Firebase 프로젝트는 연결될 수 있지만, `ApiEndpoints.kt`가 placeholder 상태라면 백엔드 API 호출은 실패합니다.

## 개발자가 해야 할 일

실제 앱 기능을 확인하려면 아래 파일의 모든 `https://example.com/...` 값을 팀에서 사용하는 실제 API URL로 바꿔야 합니다.

```text
app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt
```

예시:

```kotlin
const val LOGIN_API = "https://실제-login-api-url"
const val SIGNUP_API = "https://실제-signup-api-url"
```

## 같이 확인할 파일

- `app/google-services.json`: Firebase 프로젝트 설정
- `secrets.properties`: Naver Maps / NCP 키
- `app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt`: 백엔드 API 주소

이 세 가지가 모두 맞아야 실제 로그인, 회원가입, 지도, 코스 저장 흐름이 정상 동작합니다.

## 빠른 확인법

아래 명령어로 아직 placeholder가 남아 있는지 확인합니다.

```powershell
rg -n "example.com" app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt
```

결과가 나오면 아직 실제 백엔드 주소가 들어가지 않은 상태입니다.

## 공개 레포에서 실제 URL을 숨긴 이유

초기 공개 레포 생성 요청에서 민감정보를 모두 가리기로 했기 때문에 실제 Firebase/Naver/API URL을 제거했습니다.

다만 실제 앱처럼 동작시키려면 개발자별 로컬 설정 또는 팀 내부 문서로 실제 API URL을 주입해야 합니다.

