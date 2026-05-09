package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json


// 🔵 서버에서 내려오는 전체 배지 정보 DTO
data class Badge(
    @Json(name = "mission_name")
    val missionName: String,

    @Json(name = "mission_description")
    val missionDescription: String,

    @Json(name = "mission_detail")
    val missionDetail: String,

    @Json(name = "progress_status")
    val progressStatus: String,

    @Json(name = "gauge_ratio")
    val gaugeRatio: Int
)

// 🔵 서버에서 내려오는 획득 배지 DTO
data class AcquiredBadge(
    @Json(name = "mission_name")
    val missionName: String? = null,

    @Json(name = "mission_description")
    val missionDescription: String? = null,

    @Json(name = "acquired_date")
    val acquiredDate: String? = null,

    // 새 배지 획득 API에서 내려오는 필드들
    @Json(name = "name")
    val name: String? = null,

    @Json(name = "description")
    val description: String? = null,

    @Json(name = "icon_url")
    val iconUrl: String? = null,

    @Json(name = "condition_type")
    val conditionType: String? = null,

    @Json(name = "condition_value")
    val conditionValue: String? = null,

    @Json(name = "badge_id")
    val badgeId: String? = null,

    @Json(name = "id")
    val id: String? = null,
)
{
    private fun firstNonBlank(vararg values: String?): String {
        return values.firstOrNull { !it.isNullOrBlank() }?.trim().orEmpty()
    }

    val displayName: String
        get() = firstNonBlank(missionName, name, badgeId, id)

    val displayDescription: String
        get() = firstNonBlank(missionDescription, description, conditionValue)

    val displayAcquiredDate: String
        get() = firstNonBlank(acquiredDate, conditionValue, conditionType)
}
