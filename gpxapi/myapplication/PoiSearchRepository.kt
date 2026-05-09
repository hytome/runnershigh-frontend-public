/*package com.pack.myapplication

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.*
import com.pack.myapplication.api.NaverSearchService
import com.pack.myapplication.ReverseGeocodingRepository // import 추가
import com.pack.myapplication.LocationInfo // LocationInfo 임포트

/**
 * POI(관심 지점) 검색 Repository (최종 버전)
 *
 * 주요 개선사항:
 * - Reverse Geocoding을 통해 추출된 상세 지역명(시/구/동/도로명)을 검색어에 포함하여 정확도 향상.
 * - 검색 반경 확대 (1km) 및 병렬 검색.
 */
class PoiSearchRepository(
    private val naverSearchService: NaverSearchService,
    private val reverseGeocodingRepository: ReverseGeocodingRepository, // ReverseGeocodingRepository 주입
    private val context: Context
) {

    companion object {
        private const val SEARCH_RADIUS_M = 1000.0  // 1km
        private const val MARKER_DISPLAY_RADIUS_M = 100.0  // 100m
        private const val EARLY_EXIT_DISTANCE_M = 20.0  // 20m

        /**
         * 검색할 키워드 목록
         */
        private val PRIORITY_KEYWORDS = listOf(
            "편의점", "카페", "음식점",
            "공원", "광장", "체육시설",
            "마트", "백화점", "쇼핑몰",
            "은행", "병원", "약국", "우체국",
            "지하철역", "버스정류장", "주차장",
            "랜드마크", "관광명소", "유명장소",
            "박물관", "도서관", "극장",
            "학교", "교회", "주유소"
        )

        // 동시 검색 키워드 수
        private const val CONCURRENT_SEARCHES = 5
    }

    /**
     * 현재 위치 주변의 가장 가까운 시설을 검색합니다.
     *
     * @param currentLatLng 현재 위치 (WGS84 좌표)
     * @return 가장 가까운 시설의 좌표와 이름, 또는 null
     */
    suspend fun searchNearbyPoi(currentLatLng: LatLng): Pair<LatLng, String>? {
        showToast("주변 시설 검색 중...")

        // ⭐ Reverse Geocoding을 통해 상세 지역 정보 추출
        val locationInfo = reverseGeocodingRepository.getLocationInfoFromLatLng(currentLatLng)
        val areaFilter = locationInfo?.let { "${it.area1} ${it.area2} ${it.area3}" }

        Log.d("PoiRepo", "=== 병렬 검색 시작 ===")
        Log.d("PoiRepo", "현재 위치: ${currentLatLng.latitude}, ${currentLatLng.longitude}")
        Log.d("PoiRepo", "추출된 지역 필터: ${areaFilter ?: "없음"}")
        Log.d("PoiRepo", "검색 반경: ${SEARCH_RADIUS_M.toInt()}m")
        Log.d("PoiRepo", "표시 반경: ${MARKER_DISPLAY_RADIUS_M.toInt()}m")

        var closestPoi: Pair<LatLng, String>? = null
        var closestDistance = Double.MAX_VALUE
        var totalSearched = 0

        // 키워드를 배치 단위로 나누어 병렬 검색
        PRIORITY_KEYWORDS.chunked(CONCURRENT_SEARCHES).forEachIndexed { batchIndex, batch ->
            val progress = ((batchIndex + 1) * 100 / (PRIORITY_KEYWORDS.size / CONCURRENT_SEARCHES).coerceAtLeast(1))
            showToast("검색 중... ($progress%)")

            Log.d("PoiRepo", "=== 배치 ${batchIndex + 1} 시작 ===")

            val results = withContext(Dispatchers.IO) {
                batch.map { keyword ->
                    async {
                        // 지역 정보를 포함하여 검색 함수 호출
                        searchKeyword(keyword, currentLatLng, locationInfo)
                    }
                }.awaitAll()
            }

            totalSearched += batch.size

            // 결과 중 가장 가까운 것 찾기
            results.forEach { result ->
                if (result != null) {
                    val (poi, distance, keyword) = result
                    if (distance < closestDistance) {
                        closestPoi = poi
                        closestDistance = distance
                        Log.d("PoiRepo", "  ✓ 신기록! ${poi.second} (${distance.toInt()}m) [$keyword]")
                    }
                }
            }

            // 조기 종료: 20m 이내 발견
            if (closestDistance <= EARLY_EXIT_DISTANCE_M) {
                Log.d("PoiRepo", "✓ ${closestDistance.toInt()}m 이내 발견! 검색 종료")
                showToast("✓ 바로 근처에 발견!")
                return@forEachIndexed
            }
        }

        Log.d("PoiRepo", "=== 검색 완료 ===")
        Log.d("PoiRepo", "검색한 키워드: $totalSearched")

        // 최종 필터링: 100m 이내만 반환
        if (closestPoi != null) {
            val title = closestPoi!!.second
            val distanceInt = closestDistance.toInt()

            Log.d("PoiRepo", "가장 가까운 시설: $title (${distanceInt}m)")

            if (closestDistance <= MARKER_DISPLAY_RADIUS_M) {
                Log.d("PoiRepo", "✓ 경유지로 적합! 마커 표시")
                showToast("✓ $title (${distanceInt}m)", Toast.LENGTH_LONG)
                return closestPoi
            } else {
                Log.d("PoiRepo", "⚠️ 가장 가까운 시설(${distanceInt}m)이 100m 밖")
                showToast("가장 가까운 시설(${distanceInt}m)이 조금 멀어요")
            }
        } else {
            Log.w("PoiRepo", "1km 이내 시설 없음")
        }

        showToast("❌ 100m 이내 시설 없음")
        return null
    }

    /**
     * 단일 키워드로 POI를 검색합니다.
     *
     * 상세 지역 정보를 활용하여 다양한 필터링 전략을 순차적으로 시도합니다.
     *
     * @param keyword 검색 키워드 (예: "편의점", "카페")
     * @param currentLatLng 현재 위치
     * @param locationInfo Reverse Geocoding으로 얻은 상세 지역 정보
     * @return (POI 정보, 거리, 키워드) 또는 null
     */
    private suspend fun searchKeyword(
        keyword: String,
        currentLatLng: LatLng,
        locationInfo: LocationInfo?
    ): Triple<Pair<LatLng, String>, Double, String>? {

        // 검색 필터 전략 목록
        val searchFilters = mutableListOf<String>()

        if (locationInfo != null) {
            // 1. 전략 (가장 정확): 지번 주소 기반 상세 필터 (시/도 + 구/군 + 동/읍/면 + 키워드)
            searchFilters.add("${locationInfo.area1} ${locationInfo.area2} ${locationInfo.area3} $keyword")

            // 2. 전략 (도로명): 도로명 주소 기반 상세 필터 (시/도 + 구/군 + 도로명 + 키워드)
            locationInfo.roadName?.let { roadName ->
                searchFilters.add("${locationInfo.area1} ${locationInfo.area2} $roadName $keyword")
            }

            // 3. 전략 (광역): 시/도 기반 광역 필터 (fallback)
            searchFilters.add("${locationInfo.area1} $keyword")
        }

        // 4. 전략 (전국 검색): 키워드만 검색 (최종 fallback)
        searchFilters.add(keyword)


        for (searchQuery in searchFilters.distinct()) {
            val result = executeSearch(searchQuery, keyword, currentLatLng)
            if (result != null) {
                // 성공적으로 결과를 찾으면 즉시 반환하여 다음 전략 시도를 중단
                return result
            }
        }

        return null
    }

    /**
     * 실제로 네이버 검색 API를 호출하고 가장 가까운 결과를 반환하는 내부 함수
     */
    private suspend fun executeSearch(
        searchQuery: String,
        originalKeyword: String,
        currentLatLng: LatLng
    ): Triple<Pair<LatLng, String>, Double, String>? {
        return try {
            Log.d("PoiRepo", "  검색 시도: '$searchQuery'")

            val response = naverSearchService.searchLocal(
                query = searchQuery,
                display = 10,
                start = 1,
                sort = "random",
                x = currentLatLng.longitude,
                y = currentLatLng.latitude
            )

            if (response.isSuccessful) {
                val items = response.body()?.items

                if (items != null && items.isNotEmpty()) {
                    var closest: Pair<LatLng, String>? = null
                    var minDistance = Double.MAX_VALUE

                    for (item in items) {
                        try {
                            val mapX = item.mapx.toDoubleOrNull()
                            val mapY = item.mapy.toDoubleOrNull()

                            if (mapX == null || mapY == null) continue

                            val poiLatLng = LocationUtils.convertKatecToLatLng(mapX, mapY)

                            if (poiLatLng.latitude == 0.0 && poiLatLng.longitude == 0.0) continue

                            val distance = currentLatLng.distanceTo(poiLatLng)
                            val title = item.title.replace("<b>", "").replace("</b>", "")

                            Log.d("PoiRepo", "    - $title: ${distance.toInt()}m (필터: '$searchQuery')")

                            // 1km 이내이고 가장 가까운지 확인
                            if (distance <= SEARCH_RADIUS_M && distance < minDistance) {
                                closest = Pair(poiLatLng, title)
                                minDistance = distance
                            }

                        } catch (e: Exception) {
                            Log.w("PoiRepo", "POI 처리 오류: ${item.title}")
                        }
                    }

                    if (closest != null) {
                        // 결과를 찾았을 경우, 이 검색 전략의 결과를 반환
                        return Triple(closest, minDistance, originalKeyword)
                    }
                } else {
                    Log.d("PoiRepo", "    → 결과 없음 (필터: '$searchQuery')")
                }
            } else {
                Log.e("PoiRepo", "    → API 오류: ${response.code()}")
            }

            null

        } catch (e: Exception) {
            Log.w("PoiRepo", "검색 실패: ${originalKeyword} - ${e.message}")
            null
        }
    }


    /**
     * Toast 메시지를 메인 스레드에서 표시합니다.
     */
    private suspend fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, duration).show()
        }
        // UI가 업데이트되도록 짧은 딜레이 추가
        delay(300)
    }
}*/