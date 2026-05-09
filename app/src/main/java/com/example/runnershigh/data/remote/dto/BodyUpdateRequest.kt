package com.example.runnershigh.data.remote.dto

data class BodyUpdateRequest(
    val user_uuid: String,
    val height: Int,
    val weight: Int
)
