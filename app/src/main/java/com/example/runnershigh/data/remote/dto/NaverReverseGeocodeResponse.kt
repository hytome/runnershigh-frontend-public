package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NaverReverseGeocodeResponse(
    @Json(name = "results")
    val results: List<NaverReverseGeocodeResult>? = null
)

@JsonClass(generateAdapter = true)
data class NaverReverseGeocodeResult(
    @Json(name = "region")
    val region: NaverReverseGeocodeRegion? = null
)

@JsonClass(generateAdapter = true)
data class NaverReverseGeocodeRegion(
    @Json(name = "area1")
    val area1: NaverReverseGeocodeArea? = null,
    @Json(name = "area2")
    val area2: NaverReverseGeocodeArea? = null
)

@JsonClass(generateAdapter = true)
data class NaverReverseGeocodeArea(
    @Json(name = "name")
    val name: String? = null
)
