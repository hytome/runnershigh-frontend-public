package com.example.runnershigh.domain.model

import com.naver.maps.geometry.LatLng

/**
 * 러닝 중 위치 상태를 나타내는 데이터 클래스.
 * - isTracking: 현재 위치 추적이 활성화되어 있는지
 * - pathPoints: 지도에 그릴 모든 좌표
 * - totalDistanceMeters: 누적 거리 (미터)
 */
data class RunningLocationState(
    val isTracking: Boolean = false,
    val pathPoints: List<LatLng> = emptyList(),
    val totalDistanceMeters: Double = 0.0,
    val movingTimeSec: Int = 0,
    val currentElevationM: Double = 0.0,
    val totalElevationGainM: Double = 0.0,
    val gpxPoints: List<GpxLocationPoint> = emptyList(),
    val lastLocationTimeMs: Long? = null
)
