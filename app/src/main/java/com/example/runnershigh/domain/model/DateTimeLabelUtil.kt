package com.example.runnershigh.domain.model

fun formatDateLabel(date: String): String {
    // "2025-01-21" -> "2025년 1월 21일"
    val parts = date.split("-")
    if (parts.size != 3) return date

    val (y, m, d) = parts
    return "${y}년 ${m.toInt()}월 ${d.toInt()}일"
}