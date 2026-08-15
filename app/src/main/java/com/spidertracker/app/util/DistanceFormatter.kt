package com.spidertracker.app.util

import java.util.*

object DistanceFormatter {
    fun format(meters: Double): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            String.format(Locale.US, "%.1f km", meters / 1000)
        }
    }
}
