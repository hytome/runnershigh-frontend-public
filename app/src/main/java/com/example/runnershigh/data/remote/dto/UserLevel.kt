package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json
data class UserLevel(

    @Json(name = "level")
    val level: Int,

    @Json(name = "current_xp")
    val currentXp: Int,
    @Json(name = "next_level_xp")
    val nextLevelXp: Int,

    @Json(name = "progress")
    val progress: Double,

    // ✅ 백엔드 응답이 변경되더라도 파싱 오류가 발생하지 않도록 선택적 필드로 처리
    @Json(name = "user_id")
    val userId: String? = null

    )