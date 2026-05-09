package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

/**
 * 배지 자동 획득 검사를 수행하기 위한 요청 모델.
 * 서버에서 세션 기록을 기반으로 배지를 지급할 수 있도록
 * 사용자와 최근 세션 정보(선택)를 함께 전달한다.
 */
data class AcquireBadgeRequest(
    @Json(name = "user_uuid")
    val userUuid: String,

    @Json(name = "sessions")
    val sessions: List<BadgeSessionRecord> = emptyList()
)

/**
 * 배지 자동 획득 검사 응답.
 * 새롭게 획득한 배지 리스트를 내려보내도록 정의한다.
 */
data class AcquireBadgeResponse(
    @Json(name = "new_badges")
    val newBadges: List<AcquiredBadge> = emptyList(),

    @Json(name = "success")
    val success: Boolean = false,

    @Json(name = "message")
    val message: String? = null
)

/**
 * 서버에서 배지 조건 검증에 사용할 세션 기록 요약.
 */
data class BadgeSessionRecord(
    @Json(name = "session_id")
    val sessionId: String,

    @Json(name = "distance_km")
    val distanceKm: Double,

    @Json(name = "duration_sec")
    val durationSec: Int,

    @Json(name = "date")
    val date: String
)
