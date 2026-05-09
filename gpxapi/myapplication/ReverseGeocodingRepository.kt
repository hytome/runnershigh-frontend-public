// ReverseGeocodingRepository.kt
/*package com.pack.myapplication

import android.util.Log
import com.naver.maps.geometry.LatLng
import com.pack.myapplication.api.ReverseGeocodingResponse

import com.pack.myapplication.LocationInfo // LocationInfo 임포트
import com.pack.myapplication.api.ReverseGeocodingService
import java.io.IOException

class ReverseGeocodingRepository(
    private val reverseGeocodingService: ReverseGeocodingService
) {
    private val TAG = "ReverseGeoRepo"

    /**
     * 좌표를 상세 지역 정보 객체로 변환합니다.
     * @param latLng 변환할 좌표 (위도, 경도)
     * @return LocationInfo 객체 (지역/도로명 정보 포함) 또는 null
     */
    suspend fun getLocationInfoFromLatLng(latLng: LatLng): LocationInfo? {
        // Naver Reverse Geocoding API는 "경도,위도" 포맷을 사용합니다.
        val coords = "${latLng.longitude},${latLng.latitude}"

        return try {
            // 요청 시 orders에 'addr'(지번), 'roadaddr'(도로명) 포함 필수
            val response = reverseGeocodingService.reverseGeocode(
                coords = coords,
                orders = "addr,roadaddr"
            )

            if (response.status.code == 0 && response.results.isNotEmpty()) {
                val roadAddressResult = response.results.find { it.name == "roadaddr" }
                val jibunAddressResult = response.results.find { it.name == "addr" }

                // 도로명 주소 기반 정보 추출 시도
                roadAddressResult?.let { result ->
                    val region = result.region
                    val land = result.land
                    if (region != null && land != null) {
                        return LocationInfo(
                            area1 = region.area1.name,
                            area2 = region.area2.name,
                            area3 = region.area3.name,
                            roadName = land.roadName
                        )
                    }
                }

                // 도로명 주소가 없으면 지번 주소 기반 정보 추출 시도
                jibunAddressResult?.let { result ->
                    val region = result.region
                    if (region != null) {
                        return LocationInfo(
                            area1 = region.area1.name,
                            area2 = region.area2.name,
                            area3 = region.area3.name,
                            roadName = null // 지번 주소에는 도로명 없음
                        )
                    }
                }
                null // 두 가지 방법 모두 실패
            } else {
                Log.e(TAG, "Reverse Geocoding API 오류: ${response.status.message} (Code: ${response.status.code})")
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "네트워크 오류: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "알 수 없는 오류: ${e.message}")
            null
        }
    }
}*/