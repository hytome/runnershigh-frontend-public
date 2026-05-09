# RunnersHigh Android Frontend

Public Android frontend project for RunnersHigh.

## Included

- Android app module: `app`
- Wear OS companion module: `wear`
- Gradle wrapper and version catalog
- Compose UI, app navigation, repositories, DTOs, and device integrations used by the app

## Not Included

- Firebase project credentials
- Naver Maps credentials
- Cloud Functions/backend source
- Release builds, APK/AAB files, keystores, and generated build output

## Local Setup

1. Copy `secrets.properties.example` to `secrets.properties`.
2. Fill in the Naver Maps values in `secrets.properties`.
3. Copy `app/google-services.example.json` to `app/google-services.json`.
4. Replace the Firebase placeholder values in `app/google-services.json`.
5. Replace the placeholder API URLs in `app/src/main/java/com/example/runnershigh/data/remote/ApiEndpoints.kt` with your backend endpoints if you are running the app against a real server.

`secrets.properties` and `app/google-services.json` are intentionally ignored by Git.
The Firebase Gradle plugin is applied only when `app/google-services.json` exists, so the project can be opened and built from a clean public clone.

## Build

```powershell
.\gradlew.bat :app:assembleDebug
```

```powershell
.\gradlew.bat :wear:assembleDebug
```
