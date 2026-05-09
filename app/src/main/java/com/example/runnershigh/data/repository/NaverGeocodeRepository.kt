package com.example.runnershigh.data.repository

import android.util.Log
import com.example.runnershigh.BuildConfig
import com.example.runnershigh.data.remote.api.NaverGeocodeApi
import java.io.IOException
import retrofit2.HttpException

class NaverGeocodeRepository(
    private val geocodeApi: NaverGeocodeApi
) {

    suspend fun fetchRegionLabel(latitude: Double, longitude: Double): String? {
        if (BuildConfig.NAVER_MAPS_KEY_ID.isBlank() || BuildConfig.NAVER_MAPS_KEY.isBlank()) {
            Log.w("NaverGeocodeRepository", "Naver Maps API keys are missing; skipping reverse geocode.")
            return null
        }
        return try {
            val response = geocodeApi.reverseGeocode(coords = "${longitude},${latitude}")
            val region = response.results
                ?.firstOrNull()
                ?.region
            val area1 = region?.area1?.name?.trim().orEmpty()
            val area2 = region?.area2?.name?.trim().orEmpty()
            val area3 = region?.area3?.name?.trim().orEmpty()
            val parts = listOfNotNull(
                area1.takeIf { it.isNotBlank() },
                area2.takeIf { it.isNotBlank() },
                area3.takeIf { it.isNotBlank() }
            )
            parts.takeIf { it.isNotEmpty() }?.joinToString(" ")
        } catch (exception: HttpException) {
            if (exception.code() == 401) {
                val errorBody = exception.response()?.errorBody()?.string().orEmpty()
                Log.w(
                    "NaverGeocodeRepository",
                    "Reverse geocode unauthorized. Check NCP subscription and API key ID/secret mapping. $errorBody"
                )
            } else {
                Log.w("NaverGeocodeRepository", "Reverse geocode failed: HTTP ${exception.code()}")
            }
            null
        } catch (exception: IOException) {
            Log.w("NaverGeocodeRepository", "Reverse geocode failed due to network error.", exception)
            null
        }
    }
}
