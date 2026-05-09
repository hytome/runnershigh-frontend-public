/*package com.pack.myapplication

import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import org.locationtech.proj4j.*
import android.util.Log

object LocationUtils {

    // Proj4J 초기화
    private val crsFactory = CRSFactory()

    // WGS84 좌표계 (EPSG:4326) - GPS가 사용하는 좌표계
    private val wgs84CRS = crsFactory.createFromName("EPSG:4326")

    // KATEC 좌표계 (TM-128, EPSG:5179) - 네이버 API가 사용하는 좌표계
    private val katecCRS = crsFactory.createFromParameters(
        "KATEC",
        "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs"
    )

    // 좌표 변환기
    private val wgs84ToKatecTransformer = BasicCoordinateTransform(wgs84CRS, katecCRS)
    private val katecToWgs84Transformer = BasicCoordinateTransform(katecCRS, wgs84CRS)

    /**
     * KATEC (TM-128) 좌표를 WGS84 (LatLng) 좌표로 변환합니다.
     *
     * 사용처: 네이버 검색 API 응답(KATEC 좌표)을 지도에 표시하기 위해 변환
     *
     * @param mapX KATEC X 좌표
     * @param mapY KATEC Y 좌표
     * @return WGS84 위도/경도
     */
    fun convertKatecToLatLng(mapX: Double, mapY: Double): LatLng {
        try {
            // 방법 1: 네이버 SDK의 내장 변환 사용 (가장 정확)
            val tm128Coord = Tm128(mapX, mapY)
            return tm128Coord.toLatLng()

        } catch (e: Exception) {
            Log.e("LocationUtils", "KATEC to LatLng 변환 실패 (네이버 SDK), Proj4J 시도: $e")

            try {
                // 방법 2: Proj4J 사용 (백업)
                val srcCoord = ProjCoordinate(mapX, mapY)
                val dstCoord = ProjCoordinate()

                katecToWgs84Transformer.transform(srcCoord, dstCoord)

                return LatLng(dstCoord.y, dstCoord.x)  // 위도, 경도 순서

            } catch (e2: Exception) {
                Log.e("LocationUtils", "KATEC to LatLng 변환 완전 실패: $e2")
                return LatLng(0.0, 0.0)
            }
        }
    }

    /**
     * WGS84 (LatLng) 좌표를 KATEC (TM-128) 좌표로 변환합니다.
     *
     * 사용처: GPS 위치를 네이버 좌표계로 변환 (현재는 미사용)
     *
     * @param lat 위도
     * @param lng 경도
     * @return KATEC (Tm128) 좌표
     */
    fun convertLatLngToKatec(lat: Double, lng: Double): Tm128 {
        try {
            // Proj4J로 변환
            val srcCoord = ProjCoordinate(lng, lat)  // ⚠️ Proj4J는 경도, 위도 순서!
            val dstCoord = ProjCoordinate()

            wgs84ToKatecTransformer.transform(srcCoord, dstCoord)

            return Tm128(dstCoord.x, dstCoord.y)

        } catch (e: Exception) {
            Log.e("LocationUtils", "LatLng to KATEC 변환 실패: $e", e)
            return Tm128(0.0, 0.0)
        }
    }

    /**
     * 변환 테스트용 함수
     */
    fun testConversion() {
        // 서울 시청 좌표로 테스트
        val testLat = 37.5665
        val testLng = 126.9780

        Log.d("LocationUtils", "=== 좌표 변환 테스트 ===")
        Log.d("LocationUtils", "원본 WGS84: ($testLat, $testLng)")

        // WGS84 → KATEC
        val katecCoord = convertLatLngToKatec(testLat, testLng)
        Log.d("LocationUtils", "변환 KATEC: (${katecCoord.x}, ${katecCoord.y})")

        // KATEC → WGS84 (역변환)
        val wgs84Coord = convertKatecToLatLng(katecCoord.x, katecCoord.y)
        Log.d("LocationUtils", "역변환 WGS84: (${wgs84Coord.latitude}, ${wgs84Coord.longitude})")

        // 오차 계산
        val latDiff = Math.abs(testLat - wgs84Coord.latitude)
        val lngDiff = Math.abs(testLng - wgs84Coord.longitude)
        Log.d("LocationUtils", "오차: 위도 ${latDiff}, 경도 ${lngDiff}")
    }
}*/