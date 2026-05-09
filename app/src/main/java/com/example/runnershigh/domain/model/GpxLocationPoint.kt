package com.example.runnershigh.domain.model

/**
 * GPX 파일 생성을 위한 위치 정보.
 *  - 위도/경도는 지도 표시 및 누적 거리 계산에 사용
 *  - 고도/시간은 GPX <ele>, <time> 태그에 그대로 기록
 */
data class GpxLocationPoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double = 0.0,
    val timestampIsoUtc: String
)
