// ReverseGeocodingService.kt

/*package com.pack.myapplication.api

// 정의할 응답 모델 임포트
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
interface ReverseGeocodingService {
    /**
     * 네이버 클라우드 플랫폼 Maps Reverse Geocoding API 호출
     * 좌표(coords)를 주소로 변환합니다.
     *
     * @param coords 경도,위도 (예: "127.585,34.9765")
     * @param output 응답 형식 (기본값: json)
     * @param orders 요청할 주소 결과 타입 (legalcode, admcode, addr, roadaddr)
     * @return ReverseGeocodingResponse 객체
     */
    @GET("map-reversegeocode/v2/gc")
    suspend fun reverseGeocode(
        @Query("coords") coords: String,
        @Query("output") output: String = "json",
        @Query("orders") orders: String = "legalcode,admcode,addr,roadaddr"
    ): ReverseGeocodingResponse
}

// ReverseGeocodingResponse.kt





// 최상위 응답 객체
data class ReverseGeocodingResponse(
    @SerializedName("status")
    val status: Status,
    @SerializedName("results")
    val results: List<Result> // 결과 리스트 (주소 타입별로 항목이 들어감)
)

// API 상태 정보
data class Status(
    @SerializedName("code")
    val code: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("message")
    val message: String
)

// 각 주소 타입별 결과 항목
data class Result(
    @SerializedName("name")
    val name: String, // 요청한 orders 값 (예: 'addr', 'roadaddr')
    @SerializedName("code")
    val code: Code? = null,
    @SerializedName("region")
    val region: Region? = null,
    @SerializedName("land")
    val land: Land? = null
)

// 주소 코드 (legalcode/admcode 요청 시)
data class Code(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("mappingId")
    val mappingId: String
)

// 지역 정보 (addr/roadaddr 요청 시)
data class Region(
    @SerializedName("area1") // 시/도
    val area1: Area,
    @SerializedName("area2") // 시/군/구
    val area2: Area,
    @SerializedName("area3") // 읍/면/동
    val area3: Area,
    @SerializedName("area4") // 리
    val area4: Area
)

data class Area(
    @SerializedName("name")
    val name: String,
    @SerializedName("alias")
    val alias: String,
    @SerializedName("coords")
    val coords: Coords
)

data class Coords(
    @SerializedName("center")
    val center: Center
)

data class Center(
    @SerializedName("crs")
    val crs: String,
    @SerializedName("x")
    val x: Double,
    @SerializedName("y")
    val y: Double
)

// 상세 주소 정보 (addr/roadaddr 요청 시)
data class Land(
    @SerializedName("type")
    val type: String, // 'A' (주소), 'R' (도로명주소)
    @SerializedName("number1")
    val number1: String, // 지번 본번
    @SerializedName("number2")
    val number2: String, // 지번 부번
    @SerializedName("name")
    val name: String, // 명칭 (예: 동/리 이름)
    @SerializedName("coords")
    val coords: Coords,
    @SerializedName("addition0")
    val addition0: Addition? = null, // 건물 정보
    @SerializedName("addition1")
    val addition1: Addition? = null, // 상세 주소 정보
    @SerializedName("addition2")
    val addition2: Addition? = null, // 상세 주소 정보
    @SerializedName("addition3")
    val addition3: Addition? = null, // 상세 주소 정보
    @SerializedName("addition4")
    val addition4: Addition? = null, // 상세 주소 정보
    @SerializedName("roadName")
    val roadName: String? = null, // 도로명
    @SerializedName("isVip")
    val isVip: Boolean
)

data class Addition(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
)*/