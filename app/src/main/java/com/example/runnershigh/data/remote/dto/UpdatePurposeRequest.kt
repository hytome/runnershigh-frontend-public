package com.example.runnershigh.data.remote.dto

data class UpdatePurposeRequest(
    val user_uuid: String,
    val runningPurpose: List<String>
)
