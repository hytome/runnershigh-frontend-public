/*package com.pack.myapplication.model

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrl
import kotlinx.coroutines.*
import java.io.IOException

// OkHttpClient 인스턴스는 앱 전체에서 싱글톤으로 사용하는 것이 좋습니다.
private val client = OkHttpClient()

/**
 * 네이버 지도 길찾기 API에 요청을 보내고 JSON 응답 문자열을 반환합니다.
 *
 * @param start 시작 위치 좌표 (예: "127.105432,37.359043")
 * @param goal 도착 위치 좌표 (예: "127.105432,37.359043")
 * @return 성공 시 응답 본문(String), 실패 시 예외를 포함하는 Result<String>
 */
suspend fun directionCallRequest(start: String, goal: String): Result<String> {

    // 1. URL 구성
    val urlBuilder = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving".toHttpUrl().newBuilder()

    urlBuilder.addQueryParameter("start", start)
    urlBuilder.addQueryParameter("goal", goal)
    urlBuilder.addQueryParameter("option", "trafast") // 빠른 길 옵션

    val url = urlBuilder.build()

    // 2. Request 구성 및 Header 추가
    val request = Request.Builder()
        .url(url)
        // !!! 반드시 실제 네이버 API Key로 대체해야 합니다. !!!
        .addHeader("X-NCP-APIGW-API-KEY-ID", "YOUR_NCP_CLIENT_ID")
        .addHeader("X-NCP-APIGW-API-KEY", "YOUR_NCP_CLIENT_SECRET")
        .build()

    // 3. 네트워크 호출 및 예외 처리
    return try {
        // 네트워크 작업은 IO 디스패처에서 실행
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }

        // 4. 응답 처리 및 Result 반환
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                // 성공 (Result<String>)
                Result.success(responseBody)
            } else {
                // 응답 본문이 비어있는 경우. Result<String> 타입 명시
                Result.failure<String>(IOException("Empty response body"))
            }
        } else {
            // HTTP 상태 코드 오류 (4xx, 5xx 등). Result<String> 타입 명시
            Result.failure<String>(IOException("Status code error: ${response.code}, message: ${response.message}"))
        }
    } catch (e: IOException) {
        // 네트워크 연결 오류, 타임아웃, DNS 문제 등. Result<String> 타입 명시
        Result.failure<String>(e)
    }
}*/
