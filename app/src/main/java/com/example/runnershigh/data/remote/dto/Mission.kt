package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

data class Mission(
    @Json(name = "mission_name")
    val missionName: String? = null,

    @Json(name = "id")
    val planId: String? = null,

    @Json(name = "mission_description")
    val missionDescription: String? = null,

    @Json(name = "mission_detail")
    val missionDetail: String? = null,

    // 새 API 스펙에 맞춘 필드들 (예: base_distance, xp_reward 등)
    @Json(name = "title")
    val title: String? = null,

    @Json(name = "description")
    val description: String? = null,

    @Json(name = "progress_status")
    val progressStatus: String? = null,

    @Json(name = "exp_points")
    val expPoints: Int = 0,

    @Json(name = "xp_reward")
    val xpReward: Int? = null,

    @Json(name = "mission_category")
    val missionCategory: String? = null,

    @Json(name = "gauge_ratio")
    val gaugeRatio: Int = 0,   // 0~100 between percent

    @Json(name = "base_distance")
    val baseDistanceKm: Double? = null,

    @Json(name = "target_pace_sec_per_km")
    val targetPaceSecPerKm: Int? = null,

    @Json(name = "goal_type")
    val goalType: String? = null,

    @Json(name = "base_type")
    val baseType: String? = null,

    @Json(name = "day")
    val day: Int? = null,

    @Json(name = "week")
    val week: Int? = null,
)

data class MissionListResponse(
    val missions: List<Mission>
)