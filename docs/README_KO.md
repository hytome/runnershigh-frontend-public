# 문서 목차

러너스하이 Android 프론트엔드 개발자가 필요한 문서를 빠르게 찾기 위한 목차입니다.

## 처음 세팅하는 개발자

1. [로컬 실행 가이드](SETUP_KO.md)
2. [민감정보 설정](SECRETS_KO.md)
3. [프로젝트 구조](PROJECT_STRUCTURE_KO.md)
4. [로그인 405 / 회원가입 404 문제 해결](RUNTIME_API_TROUBLESHOOTING_KO.md)

## 기능을 수정하는 개발자

- [기능별 코드 지도](FEATURE_MAP_KO.md)
- [코스/GPX 가이드](COURSE_GPX_KO.md)

## 협업과 공개 레포 관리

- [프론트 개발 협업 규칙](CONTRIBUTING_KO.md)
- [공개 레포 업로드 체크리스트](PUBLIC_RELEASE_CHECKLIST_KO.md)

## 자주 나는 문제

- 로그인에서 `405`가 나면 `ApiEndpoints.kt`가 아직 `example.com`인지 확인합니다.
- 회원가입 마지막에 `404`가 나면 `SIGNUP_API`, `UPDATE_BODY_API`, `UPDATE_PURPOSE_API`, `UPDATE_EXPERIENCE_API` 주소를 확인합니다.
- Firebase 로그인 설정은 `google-services.json`, 백엔드 API 호출은 `ApiEndpoints.kt`가 담당합니다. 둘은 별개입니다.

## 빠른 기준

- 앱 화면을 고칠 때는 `app/src/main/java/com/example/runnershigh/ui/screen`부터 봅니다.
- 화면 이동을 고칠 때는 `app/src/main/java/com/example/runnershigh/navigation/NavGraph.kt`를 봅니다.
- 서버 통신을 고칠 때는 `data/remote`, `data/repository`를 같이 봅니다.
- 코스 저장과 GPX를 고칠 때는 `RunningViewModel`, `RunningRepository`, `GpxManager`를 같이 봅니다.
- Wear 심박수 연동을 고칠 때는 `wear` 모듈과 `app/data/wear`, `app/data/heartrate`를 같이 봅니다.

