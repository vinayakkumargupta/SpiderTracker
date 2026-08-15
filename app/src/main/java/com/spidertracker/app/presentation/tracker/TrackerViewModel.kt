package com.spidertracker.app.presentation.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spidertracker.app.domain.model.LocationData
import com.spidertracker.app.domain.model.SpiderManLocation
import com.spidertracker.app.domain.usecase.FakeSpiderManTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

class TrackerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private val tracker = FakeSpiderManTracker()
    private var trackingJob: Job? = null

    fun startTracking(userLocation: LocationData, isDemo: Boolean) {
        _uiState.update { it.copy(userLocation = userLocation, isDemoMode = isDemo, isTracking = true) }
        
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            tracker.trackingFlow(userLocation).collect { spiderLoc ->
                val distance = calculateDistance(
                    userLocation.latitude, userLocation.longitude,
                    spiderLoc.latitude, spiderLoc.longitude
                )
                
                _uiState.update { state ->
                    val updatedSightings = (listOf(spiderLoc) + state.sightings).take(10)
                    val updatedTrail = (listOf(LocationData(spiderLoc.latitude, spiderLoc.longitude)) + state.webTrail).take(5)
                    state.copy(
                        spiderManLocation = spiderLoc,
                        sightings = updatedSightings,
                        webTrail = updatedTrail,
                        distanceMeters = distance,
                        currentActivity = spiderLoc.activity,
                        showNearbyEvent = distance < 500
                    )
                }
            }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        _uiState.update { it.copy(isTracking = false) }
    }

    fun dismissNearbyEvent() {
        _uiState.update { it.copy(showNearbyEvent = false) }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3 // Earth radius in meters
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}
