package com.example.runnershigh.data.remote.dto

import com.example.runnershigh.data.remote.ApiEndpoints
import retrofit2.http.Body
import retrofit2.http.POST


interface HealthApi {

    @POST(ApiEndpoints.SEND_HEALTH_DATA_API)
    suspend fun sendHealthData(
        @Body healthData: HealthData
    )
}