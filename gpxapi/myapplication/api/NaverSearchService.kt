/*package com.pack.myapplication.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverSearchService {
    @GET("v1/search/local.json")
    suspend fun searchLocal(
        @Query("query") query: String,  // ⭐ keyword → query
        @Query("display") display: Int = 5,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random",
        @Query("x") x: Double, // 경도 (Longitude)
        @Query("y") y: Double  // 위도 (Latitude)
    ): Response<NaverLocalSearchResponse>
}*/