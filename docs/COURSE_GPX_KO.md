# 코스 크리에이터와 GPX 흐름

코스 기능은 현재 메인 앱 코드와 `gpxapi` 참고 코드가 함께 존재합니다.

## 현재 메인 앱에서 사용하는 흐름

주요 파일:

- `app/src/main/java/com/example/runnershigh/ui/RunningViewModel.kt`
- `app/src/main/java/com/example/runnershigh/data/repository/RunningRepository.kt`
- `app/src/main/java/com/example/runnershigh/domain/model/GpxLocationPoint.kt`
- `app/src/main/java/com/example/runnershigh/util/GpxManager.kt`
- `app/src/main/java/com/example/runnershigh/data/remote/dto/RunningCourseDto.kt`
- `app/src/main/java/com/example/runnershigh/ui/screen/course/RegisterCourseScreen.kt`

흐름:

1. 러닝 중 GPS 좌표가 `RunningViewModel`에 누적됩니다.
2. 러닝 종료 후 최근 러닝 기록이 `lastRunForCourse`에 저장됩니다.
3. 사용자가 코스 등록 화면에서 코스 이름을 입력합니다.
4. `RunningViewModel.saveCourseFromLastRun()`이 호출됩니다.
5. `RunningRepository.createRunningCourse()`가 좌표 목록을 받아 GPX XML과 base64 문자열을 만듭니다.
6. `RunningApi.createRunningCourse()`를 통해 서버에 코스를 저장합니다.

## GPX 생성 위치

현재 앱에서 실제 사용하는 GPX 생성 코드는 여기에 있습니다.

```text
app/src/main/java/com/example/runnershigh/util/GpxManager.kt
```

`gpxapi`에도 유사한 GPX 생성 코드가 있습니다.

```text
gpxapi/myapplication/GpxManager.kt
```

`gpxapi` 쪽은 기능을 만들 때 참고한 코드로 보면 됩니다. 코스 기능을 고칠 때는 먼저 `app` 쪽 실제 사용 코드를 확인하고, 필요하면 `gpxapi`를 참고하세요.

## 코스 화면

- `RunningCourseScreen.kt`: 코스 메인
- `MyCourseScreen.kt`: 내 코스 목록
- `RegisterCourseScreen.kt`: 최근 러닝을 코스로 등록
- `AddCourseScreen.kt`: 코스 추가 진입 화면
- `CourseDetailScreen.kt`: 코스 상세
- `PopularCourseScreen.kt`: 인기 코스

## 백엔드 API

관련 endpoint 상수:

- `GET_RUNNING_COURSES`
- `CREATE_RUNNING_COURSE`
- `DELETE_COURSE`

위 값들은 `ApiEndpoints.kt`에 있습니다. 공개 레포에서는 placeholder이므로 실제 서버 주소로 바꿔야 동작합니다.

## 주의할 점

- GPX 좌표는 최소 2개 이상 있어야 의미 있는 코스가 됩니다.
- 서버가 기대하는 `pathPoints`, `gpxBase64`, 거리 단위가 프론트 DTO와 맞아야 합니다.
- Naver Maps 키가 없으면 지도 렌더링이나 지오코딩은 정상 동작하지 않을 수 있습니다.

