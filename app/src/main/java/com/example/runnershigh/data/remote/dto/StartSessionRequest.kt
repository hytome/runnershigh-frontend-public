package com.example.runnershigh.data.remote.dto
import com.squareup.moshi.Json

data class StartSessionRequest(
    @Json(name = "userId")
    val userId: String

)
