package com.example.runnershigh.data.remote.dto

import com.squareup.moshi.Json

data class NaverGeocodeResponse(
    @Json(name = "results")
    val results: List<NaverGeocodeResult> = emptyList()
)

data class NaverGeocodeResult(
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "region")
    val region: NaverGeocodeRegion? = null
)

data class NaverGeocodeRegion(
    @Json(name = "area1")
    val area1: NaverGeocodeArea? = null,
    @Json(name = "area2")
    val area2: NaverGeocodeArea? = null,
    @Json(name = "area3")
    val area3: NaverGeocodeArea? = null
)

data class NaverGeocodeArea(
    @Json(name = "name")
    val name: String? = null
)
