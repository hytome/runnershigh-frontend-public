package com.example.runnershigh.data.remote

import com.example.runnershigh.data.remote.dto.AuthApi
import com.example.runnershigh.data.remote.dto.RunningApi
import com.example.runnershigh.data.remote.dto.UserService
import com.example.runnershigh.data.remote.dto.HealthApi
import com.example.runnershigh.data.remote.api.ActivityApi
import com.example.runnershigh.data.remote.api.AnalysisApi
import com.example.runnershigh.data.remote.api.NaverGeocodeApi
import com.example.runnershigh.data.remote.ApiEndpoints
import com.example.runnershigh.BuildConfig
import okhttp3.Interceptor
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Retrofit + OkHttp 클라이언트 설정
 */
object ApiClient {

    // 에뮬레이터 → 로컬 서버 접근용 host
    private const val BASE_URL = "${ApiEndpoints.LOGIN_API}/"
    private val NAVER_MAPS_BASE_URL = BuildConfig.NAVER_MAPS_BASE_URL
    private val NAVER_MAPS_HOST = runCatching { NAVER_MAPS_BASE_URL.toHttpUrl().host }
        .getOrDefault("naveropenapi.apigw.ntruss.com")

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            redactHeader("X-NCP-APIGW-API-KEY-ID")
            redactHeader("X-NCP-APIGW-API-KEY")
        }
    }

    private val naverMapsAuthInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val request = chain.request()
            if (request.url.host == NAVER_MAPS_HOST) {
                val builder = request.newBuilder()
                if (BuildConfig.NAVER_MAPS_KEY_ID.isNotBlank()) {
                    builder.addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.NAVER_MAPS_KEY_ID)
                }
                if (BuildConfig.NAVER_MAPS_KEY.isNotBlank()) {
                    builder.addHeader("X-NCP-APIGW-API-KEY", BuildConfig.NAVER_MAPS_KEY)
                }
                return@Interceptor chain.proceed(builder.build())
            }
            chain.proceed(request)
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(naverMapsAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // ✅ Kotlin data class 를 위해 Moshi 에 KotlinJsonAdapterFactory 추가
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val naverMapsRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NAVER_MAPS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val runningApi: RunningApi by lazy {
        retrofit.create(RunningApi::class.java)
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val activityApi: ActivityApi by lazy {
        retrofit.create(ActivityApi::class.java)
    }

    val analysisApi: AnalysisApi by lazy {
        retrofit.create(AnalysisApi::class.java)
    }

    // ✅ 헬스 데이터 전송용 API
    val healthApi: HealthApi by lazy {
        retrofit.create(HealthApi::class.java)
    }

    val naverGeocodeApi: NaverGeocodeApi by lazy {
        naverMapsRetrofit.create(NaverGeocodeApi::class.java)
    }
}
