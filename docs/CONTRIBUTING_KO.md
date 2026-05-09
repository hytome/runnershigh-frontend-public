# 프론트 개발 협업 규칙

이 문서는 프론트 개발자가 작업할 때의 기본 약속입니다.

## 브랜치

기능 단위로 브랜치를 만듭니다.

```powershell
git checkout -b feature/course-ui
```

예시:

- `feature/login-ui`
- `feature/course-creator`
- `fix/running-result-crash`
- `docs/setup-guide`

## 커밋 전 확인

```powershell
git status
.\gradlew.bat :app:assembleDebug
```

Wear 쪽을 건드렸다면 같이 확인합니다.

```powershell
.\gradlew.bat :wear:assembleDebug
```

## 커밋

```powershell
git add .
git commit -m "코스 등록 화면 상태 처리 개선"
```

커밋 메시지는 한국어로 써도 괜찮습니다. 다만 무엇을 바꿨는지 한 문장으로 알아볼 수 있게 씁니다.

## Push

```powershell
git push -u origin feature/course-ui
```

## Pull Request에 적으면 좋은 내용

- 어떤 화면/기능을 바꿨는지
- 확인한 빌드 명령어
- 실제 기기/에뮬레이터에서 확인했는지
- 민감정보 파일을 건드리지 않았는지

## 커밋하면 안 되는 것

- `app/google-services.json`
- `secrets.properties`
- `local.properties`
- `app/build/`, `wear/build/`, `.gradle/`
- APK/AAB/ZIP
- keystore

## 기능을 고칠 때 보는 순서

1. 화면 파일을 찾습니다.
2. 화면이 사용하는 ViewModel을 봅니다.
3. ViewModel이 호출하는 Repository를 봅니다.
4. Repository가 사용하는 API/DTO를 봅니다.
5. 필요하면 `ApiEndpoints.kt`의 서버 주소를 확인합니다.

예를 들어 코스 등록 문제라면:

1. `RegisterCourseScreen.kt`
2. `RunningViewModel.kt`
3. `RunningRepository.kt`
4. `RunningCourseDto.kt`, `RunningApi.kt`
5. `ApiEndpoints.kt`

