package com.pack.myapplication

// GPX <trkpt>에 들어갈 데이터 모델
data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double, // 고도 (Elevation), GPX <ele> 태그
    val time: String       // ISO 8601 형식 시간 (GPX <time> 태그). "yyyy-MM-dd'T'HH:mm:ss'Z'"
)