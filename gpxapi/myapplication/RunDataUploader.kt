package com.pack.myapplication

import android.util.Log
import com.pack.myapplication.api.RetrofitClient
import com.pack.myapplication.CourseData
import com.pack.myapplication.RunRecordRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 러닝 기록 데이터를 백엔드 서버에 업로드하거나 코스 데이터를 다운로드하는 역할을 담당하는 클래스입니다.
 */
class RunDataUploader {

    private val apiService = RetrofitClient.runRecordService

    /**
     * 비동기적으로 러닝 기록 데이터를 서버에 업로드합니다.
     */
    suspend fun uploadRunData(data: RunRecordRequest): Boolean {
        // 기존 POST 로직 (변경 없음)
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.uploadRunRecord(data)
                // ... (생략: 기존 POST 성공/실패 로직)
                if (response.isSuccessful) {
                    val resultBody = response.body()
                    if (resultBody?.success == true) {
                        Log.d("RunDataUploader", "✅ 업로드 성공: Run ID = ${resultBody.courseId}")
                        true
                    } else {
                        val message = resultBody?.message ?: "Server logic failed or result body is null."
                        Log.e("RunDataUploader", "❌ 업로드 실패 - 서버 로직 오류: $message")
                        false
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RunDataUploader", "❌ 업로드 실패 - HTTP Code: ${response.code()}, Error: $errorBody")
                    false
                }
            } catch (e: Exception) {
                Log.e("RunDataUploader", "❌ 네트워크 요청 오류 발생 (POST): ${e.message}", e)
                false
            }
        }
    }

    /**
     * ⭐ [추가된 기능] 서버에서 코스 목록을 불러옵니다.
     *
     * @return List<CourseData>? 성공 시 코스 리스트, 실패 시 null
     */
    suspend fun getCourseRecords(): List<CourseData>? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. GET 요청 실행
                val response = apiService.getCourseList()

                // 2. 응답이 성공적인지 확인 (HTTP 200-299)
                if (response.isSuccessful) {
                    val resultBody = response.body()

                    // 3. 응답 바디의 success 필드와 courses 리스트 확인
                    if (resultBody?.success == true && resultBody.courses != null) {
                        Log.d("RunDataUploader", "✅ 코스 목록 불러오기 성공. ${resultBody.courses.size}개 코스 로드됨.")
                        return@withContext resultBody.courses
                    } else {
                        // 2xx 응답이지만 서버 로직에서 실패했거나 courses 필드가 null인 경우
                        val message = resultBody?.message ?: "Course list successful but no data or success=false."
                        Log.e("RunDataUploader", "❌ 코스 목록 불러오기 실패 - 서버 로직 오류: $message")
                        return@withContext null
                    }
                } else {
                    // 4xx 또는 5xx HTTP 응답 코드인 경우
                    val errorBody = response.errorBody()?.string()
                    Log.e("RunDataUploader", "❌ 코스 목록 불러오기 실패 - HTTP Code: ${response.code()}, Error: $errorBody")
                    return@withContext null
                }
            } catch (e: Exception) {
                // 네트워크 연결 오류, 타임아웃 등 요청 자체가 실패한 경우
                Log.e("RunDataUploader", "❌ 네트워크 요청 오류 발생 (GET): ${e.message}", e)
                return@withContext null
            }
        }
    }
}