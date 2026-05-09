// com.pack.myapplication.model/PoiItem.kt
/*
package com.pack.myapplication

import com.google.gson.annotations.SerializedName

/**
 * 개별 POI(Place of Interest) 정보를 담는 데이터 클래스입니다.
 */
data class PoiItem(
    // HTML 엔티티가 포함될 수 있는 상호명
    @SerializedName("title")
    val title: String,

    // API에 따라 URL이 없을 수도 있어 nullable
    @SerializedName("link")
    val link: String?,

    // 카테고리 정보 (예: "음식점>한식>냉면")
    @SerializedName("category")
    val category: String,

    // 주소 (도로명 또는 지번)
    @SerializedName("address")
    val address: String,

    // 전화번호 (nullable)
    @SerializedName("telephone")
    val telephone: String?,

    // 지번 주소
    @SerializedName("mapx")
    val mapx: String, // KATEC X 좌표 (String 형태)

    // 지번 주소
    @SerializedName("mapy")
    val mapy: String // KATEC Y 좌표 (String 형태)
) {
    /**
     * mapx, mapy가 String으로 오기 때문에 Double로 쉽게 변환할 수 있는 Getter를 제공합니다.
     */
    val katecX: Double
        get() = mapx.toDoubleOrNull() ?: 0.0

    val katecY: Double
        get() = mapy.toDoubleOrNull() ?: 0.0
}*/