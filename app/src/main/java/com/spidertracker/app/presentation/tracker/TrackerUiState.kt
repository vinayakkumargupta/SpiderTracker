package com.spidertracker.app.presentation.tracker

import com.spidertracker.app.domain.model.LocationData
import com.spidertracker.app.domain.model.SpiderActivity
import com.spidertracker.app.domain.model.SpiderManLocation

data class TrackerUiState(
    val userLocation: LocationData? = null,
    val spiderManLocation: SpiderManLocation? = null,
    val sightings: List<SpiderManLocation> = emptyList(),
    val isTracking: Boolean = false,
    val isDemoMode: Boolean = false,
    val distanceMeters: Double = 0.0,
    val currentActivity: SpiderActivity? = null,
    val showNearbyEvent: Boolean = false,
    val webTrail: List<LocationData> = emptyList()
)
