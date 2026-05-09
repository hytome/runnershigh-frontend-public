package com.example.runnershigh.domain.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize   // ⬅️ 이 import 추가

/**
 * 앱 내부에서 사용하는 러닝 세션 요약 정보
 * DTO 아님 — 화면/Repository에서 공통으로 쓰는 모델
 */
@Parcelize
data class RunningStats(
    val distanceKm: Double,     // ex: 6.0 km
    val durationSec: Int,       // ex: 1800 sec
    val paceSecPerKm: Int,      // ex: 301 (5:01)
    val calories: Int,          // ex: 404 kcal
    val avgHeartRate: Int,      // ex: 160 bpm
    val elevationGainM: Int,    // ex: 25 m
    val cadence: Int            // ex: 160 SPM
): Parcelable