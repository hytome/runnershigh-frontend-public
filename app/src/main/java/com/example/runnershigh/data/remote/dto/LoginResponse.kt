package com.example.runnershigh.data.remote.dto

data class LoginResponse(
    val user_uuid: String?,   // 성공 시 UUID
    val username: String?,    // 성공 시 유저명
    val message: String?,     // "login successful"
    val error: String?        // "Incorrect password" / "Email not found"
)
