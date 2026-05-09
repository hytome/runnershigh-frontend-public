import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

val secretProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use { load(it) }
    }
}

fun secretProperty(name: String, defaultValue: String = ""): String {
    return (secretProperties.getProperty(name) ?: project.findProperty(name) as String? ?: defaultValue)
        .trim()
}

android {
    namespace = "com.example.runnershigh"
    compileSdk = 36

    val naverMapsClientId = secretProperty("NAVER_MAPS_CLIENT_ID", "NAVER_MAPS_CLIENT_ID_PLACEHOLDER")
    val naverMapsKeyId = secretProperty("NAVER_MAPS_KEY_ID")
    val naverMapsKey = secretProperty("NAVER_MAPS_KEY")
    val naverMapsBaseUrl = secretProperty("NAVER_MAPS_BASE_URL", "https://maps.apigw.ntruss.com/")

    defaultConfig {
        applicationId = "com.example.runnershigh"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "NAVER_MAPS_KEY_ID", "\"$naverMapsKeyId\"")
        buildConfigField("String", "NAVER_MAPS_KEY", "\"$naverMapsKey\"")
        buildConfigField("String", "NAVER_MAPS_BASE_URL", "\"$naverMapsBaseUrl\"")
        manifestPlaceholders["NAVER_MAPS_CLIENT_ID"] = naverMapsClientId

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.health.connect:connect-client:1.1.0")
    implementation("com.naver.maps:map-sdk:3.23.0")
    implementation("androidx.cardview:cardview:1.0.0")
    // ★ 위치 정보(Fused Location Provider)
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // Firebase BOM (버전은 Firebase 문서에서 최신 값으로 교체)
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    // Google 로그인용
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.jakewharton.timber:timber:5.0.1")

    // OkHttp 로깅 (선택이지만 있으면 디버깅 편함)

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
}
