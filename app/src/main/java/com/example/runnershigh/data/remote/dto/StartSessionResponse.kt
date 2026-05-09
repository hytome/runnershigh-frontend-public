package com.example.runnershigh.data.remote.dto
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * 러닝 세션 시작 API 응답
 *
 * POST /sessions/start
 * 백엔드에서 새로 생성한 sessionId(or session_uuid)를 내려준다고 가정.
 * 실제 필드 이름은 백엔드와 한 번 맞춰봐야 함!
 */
data class StartSessionResponse(

    @Json(name = "sessionId")
    val sessionId: String? = null,

    @Json(name = "session_uuid")
    val sessionUuid: String? = null,

    @Json(name = "start_time")
    val startTime: String? = null,

    val message: String? = null
) {
    val resolvedSessionId: String?
        get() = sessionId ?: sessionUuid
}
