package com.spidertracker.app.domain.model

data class SpiderManLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val activity: SpiderActivity,
    val confidence: Int
)
