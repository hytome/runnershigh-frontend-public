package com.example.runnershigh.data.repository

import com.example.runnershigh.data.remote.dto.HealthApi
import com.example.runnershigh.data.remote.dto.HealthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HealthRepository(
    private val healthApi: HealthApi
) {
    suspend fun syncHealthConnectConsent(
        userUuid: String,
        consented: Boolean,
        consentedAt: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            healthApi.sendHealthData(
                HealthData(
                    userId = userUuid,
                    user_uuid = userUuid,
                    healthConnectConsented = consented,
                    consentedAt = consentedAt
                )
            )
        }
    }

    suspend fun syncHealthData(
        healthData: HealthData
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            healthApi.sendHealthData(healthData)
        }
    }
}
