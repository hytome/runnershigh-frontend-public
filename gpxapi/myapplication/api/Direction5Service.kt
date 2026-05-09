package com.pack.myapplication.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface Direction5Service {
    @GET("/map-direction/v1/driving")

    //@GET("/map-direction-15/v1/driving")//direction 15호출
    suspend fun getRoute(
        @Query("start") start: String,  // "경도,위도" 형식
        @Query("goal") goal: String,
        @Query("option") option: String = "trafast"
    ): Direction5Response
}




data class Path(
    val summary: Summary,
    val path: List<List<Double>>
)

data class Summary(
    val distance: Int,  // 미터 단위
    val duration: Int   // 밀리초 단위
)
data class Direction5Response(
    val code: Int,
    val message: String?,
    val currentDateTime: String?,
    val route: RouteResult?
)

data class RouteResult(
    val trafast: List<RoutePath>?,
    val tracomfort: List<RoutePath>?,
    val traoptimal: List<RoutePath>?
)

data class RoutePath(
    val summary: RouteSummary,
    val path: List<List<Double>>,
    val section: List<Section>?,
    val guide: List<Guide>?
)

data class RouteSummary(
    val start: Location,
    val goal: Location,
    val distance: Int,      // 전체 거리 (미터)
    val duration: Int,      // 전체 소요시간 (밀리초)
    val etaServiceType: Int?,
    val tollFare: Int?,     // 통행료
    val taxiFare: Int?,     // 택시 요금
    val fuelPrice: Int?     // 유류비
)

data class Location(
    val location: List<Double>,
    val dir: Int?
)

data class Section(
    val pointIndex: Int,
    val pointCount: Int,
    val distance: Int,
    val name: String?,
    val congestion: Int?,
    val speed: Int?
)

data class Guide(
    val pointIndex: Int,
    val type: Int,
    val instructions: String?,
    val distance: Int,
    val duration: Int
)