plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.runnershigh.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.runnershigh.wear"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Wear 모듈은 NotificationCompat 사용만 필요하여 core만 명시적으로 고정
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    implementation("androidx.wear.tiles:tiles:1.5.0-rc01")
    implementation("androidx.wear.tiles:tiles-material:1.5.0-rc01")
    implementation("androidx.wear.protolayout:protolayout-material:1.3.0-rc01")
    implementation("androidx.concurrent:concurrent-futures-ktx:1.2.0")

    implementation(platform("androidx.compose:compose-bom:2025.05.00"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.wear.compose:compose-material:1.6.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("com.google.guava:guava:33.2.1-android")
}
