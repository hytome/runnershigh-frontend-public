# 프로젝트 구조

이 레포는 크게 `app`, `wear`, `gpxapi` 세 영역으로 나뉩니다.

## app

메인 Android 휴대폰 앱입니다. 프론트 개발의 대부분은 이 모듈에서 이루어집니다.

```text
app/src/main/java/com/example/runnershigh
├─ data        API, Repository, Health Connect, Wear 연동
├─ domain      화면에서 공유하는 러닝/통계 모델
├─ navigation  Compose 화면 이동 그래프
├─ ui          Compose 화면, ViewModel, 테마, 지도 UI
└─ util        GPX, 페이스 변환 같은 공통 유틸
```

`app/src/main/res`에는 XML 레이아웃, 이미지, 아이콘, 폰트, 문자열 리소스가 들어 있습니다.

## wear

Wear OS companion 앱입니다. 휴대폰 앱과 연동해 심박수를 수집하고 Data Layer로 전달하는 역할을 합니다.

주요 파일:

- `wear/src/main/java/com/example/runnershigh/wear/MainActivity.kt`
- `wear/src/main/java/com/example/runnershigh/wear/HeartRateForegroundService.kt`
- `wear/src/main/java/com/example/runnershigh/wear/HeartRateCommandListenerService.kt`
- `wear/src/main/java/com/example/runnershigh/wear/HealthTileService.kt`

## gpxapi

코스 크리에이터, GPX 생성, POI 검색, 방향 API 실험을 위해 사용된 참고 코드입니다.

현재 메인 앱의 Gradle 모듈로 직접 묶인 코드는 아니지만, 코스 기능을 이해할 때 중요한 맥락을 제공합니다.

주요 파일:

- `gpxapi/myapplication/GpxManager.kt`
- `gpxapi/myapplication/CourseData.kt`
- `gpxapi/myapplication/RunDataUploader.kt`
- `gpxapi/myapplication/RunRecordService.kt`
- `gpxapi/myapplication/PoiSearchRepository.kt`
- `gpxapi/myapplication/api/Direction5Service.kt`

## 루트 설정 파일

- `settings.gradle.kts`: `:app`, `:wear` 모듈 포함
- `build.gradle.kts`: Android/Kotlin 플러그인 버전 선언
- `gradle/libs.versions.toml`: 버전 카탈로그
- `secrets.properties.example`: 로컬 secret 파일 예시

