package com.example.runnershigh.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.runnershigh.data.remote.dto.NaverGeocodeResponse

interface NaverGeocodeApi {
    @GET("map-reversegeocode/v2/gc")
    suspend fun reverseGeocode(
        @Query("coords") coords: String,
        @Query("output") output: String = "json",
        @Query("orders") orders: String = "legalcode"
    ): NaverGeocodeResponse
}
