package com.example.runnershigh.ui.screen.active

import java.util.Locale

internal fun formatPaceSeconds(seconds: Int): String {
    if (seconds <= 0) return "-"
    return String.format(Locale.getDefault(), "%d'%02d\"", seconds / 60, seconds % 60)
}

internal fun formatDistanceKm(distanceKm: Double, digits: Int = 1): String {
    return String.format(Locale.getDefault(), "%.${digits}fkm", distanceKm)
}

