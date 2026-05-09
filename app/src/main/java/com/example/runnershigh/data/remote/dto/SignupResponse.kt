package com.example.runnershigh.data.remote.dto

//import com.google.gson.annotations.SerializedName

data class SignupResponse(
    //@SerializedName("user_uuid")
   // val userUuid: String?,

    val user_uuid: String?,
    val email: String?,
    val username: String?,

    // 성공 시: "User created successfully"
    val message: String?,

    // 실패 시: "Email already exists" 또는 "Username already exists"
    val error: String?
)
