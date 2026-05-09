package com.example.runnershigh.util

/**
 * "6'45\"" 같은 페이스 문자열을 초 단위(Int)로 변환
 * 예: "6'45\"" -> 6 * 60 + 45 = 405
 */
fun parsePaceToSeconds(pace: String): Int {
    // 예: "6'45\"" -> 6분 45초
    val clean = pace.replace("\"", "")        // "6'45"
    val parts = clean.split("'")
    if (parts.size != 2) return 0

    val min = parts[0].toIntOrNull() ?: 0
    val sec = parts[1].toIntOrNull() ?: 0

    return min * 60 + sec
}