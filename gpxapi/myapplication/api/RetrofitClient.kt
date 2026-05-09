package com.pack.myapplication.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import java.util.concurrent.TimeUnit
import com.pack.myapplication.RunRecordService
// 이 파일은 RetrofitClient.kt입니다.
// ReverseGeocodingService, NaverSearchService, RunRecordService 인터페이스가
// com.pack.myapplication.api 패키지 내에 정의되어 있다고 가정합니다.

object RetrofitClient {

    // 로깅 인터셉터는 공통으로 사용합니다.
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /*// ====================================================================
    // 1. 네이버 클라우드 플랫폼 (NCP) Maps API 설정 (Reverse Geocoding API 사용) 🗺️
    // ====================================================================

    // NCP Maps API의 기본 URL (Reverse Geocoding V2)
    private const val NCP_MAPS_BASE_URL = "https://maps.apigw.ntruss.com/"

    private const val NCP_CLIENT_ID = "YOUR_NCP_CLIENT_ID"
    private const val NCP_CLIENT_SECRET = "YOUR_NCP_CLIENT_SECRET"

    private val ncpMapsInterceptor = Interceptor { chain ->
        val original = chain.request()

        val request = original.newBuilder()
            // NCP API 인증 헤더 추가
            .addHeader("X-NCP-APIGW-API-KEY-ID", NCP_CLIENT_ID)
            .addHeader("X-NCP-APIGW-API-KEY", NCP_CLIENT_SECRET)
            .build()

        chain.proceed(request)
    }

    private val ncpMapsOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(ncpMapsInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val ncpMapsRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NCP_MAPS_BASE_URL)
            .client(ncpMapsOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 리버스 지오코딩 서비스 인스턴스 (Repository에서 참조할 이름)
     */
    val reverseGeocodingService: ReverseGeocodingService by lazy {
        ncpMapsRetrofit.create(ReverseGeocodingService::class.java)
    }

    // ====================================================================
    // 2. 네이버 검색 API 설정 (POI 검색 API) 🔎
    // ====================================================================

    private const val NAVER_SEARCH_BASE_URL = "https://openapi.naver.com/"

    private const val NAVER_SEARCH_CLIENT_ID = "YOUR_NAVER_SEARCH_CLIENT_ID"
    private const val NAVER_SEARCH_CLIENT_SECRET = "YOUR_NAVER_SEARCH_CLIENT_SECRET"

    private val naverSearchInterceptor = Interceptor { chain ->
        val original = chain.request()

        val request = original.newBuilder()
            // 네이버 검색 API 인증 헤더 추가
            .header("X-Naver-Client-Id", NAVER_SEARCH_CLIENT_ID)
            .header("X-Naver-Client-Secret", NAVER_SEARCH_CLIENT_SECRET)
            .build()

        chain.proceed(request)
    }

    private val naverSearchOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(naverSearchInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val naverSearchRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NAVER_SEARCH_BASE_URL)
            .client(naverSearchOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * POI 검색 서비스 인스턴스
     */
    val naverSearchService: NaverSearchService by lazy {
        naverSearchRetrofit.create(NaverSearchService::class.java)
    }*/

    // ====================================================================
    // 3. 러닝 기록 업로드 API 설정 (RunRecordService) 🏃
    // ====================================================================

    // ⚠️ TODO: 실제 백엔드 서버의 기본 URL로 변경해야 합니다.
    private const val RUN_RECORD_BASE_URL = "https://your-backend-api.com/"

    // 러닝 기록 업로드는 특별한 인증 헤더가 필요 없다고 가정하고,
    // 로깅 및 타임아웃만 설정된 기본 클라이언트를 사용합니다.
    private val runRecordOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val runRecordRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(RUN_RECORD_BASE_URL)
            .client(runRecordOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 러닝 기록 업로드 서비스 인스턴스
     */
    val runRecordService: RunRecordService by lazy {
        runRecordRetrofit.create(RunRecordService::class.java)
    }
}
