package com.pack.myapplication

// 러닝 기록 요청 데이터 모델 (HTTP POST Body)
data class RunRecordRequest(
    val userId: String,
    val name: String,
    val distanceMeters: Double, // 미터(m) 단위
    val totalTimeMillis: Long, // 밀리초(ms) 단위
    val gpxFileBase64: String // Base64 인코딩된 GPX 데이터
)

// 러닝 기록 응답 데이터 모델 (HTTP Response Body)
data class RunRecordResult(
    val success: Boolean, // 서버 처리 성공 여부
    val courseId: String?, // 서버에 저장된 기록의 고유 ID
    val message: String? // 실패 시 오류 메시지
)
