package com.spidertracker.app.domain.usecase

import com.spidertracker.app.domain.model.LocationData
import com.spidertracker.app.domain.model.SpiderActivity
import com.spidertracker.app.domain.model.SpiderManLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FakeSpiderManTracker {

    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    fun trackingFlow(userLocation: LocationData): Flow<SpiderManLocation> = flow {
        // Initial position near user
        currentLat = userLocation.latitude + (Random.nextDouble() - 0.5) * 0.01
        currentLng = userLocation.longitude + (Random.nextDouble() - 0.5) * 0.01

        while (true) {
            val activity = SpiderActivity.entries.random()
            
            // Move slightly
            val angle = Random.nextDouble() * 2 * Math.PI
            val distance = Random.nextDouble() * 0.002 // Approx 200m
            
            currentLat += distance * cos(angle)
            currentLng += distance * sin(angle) / cos(Math.toRadians(currentLat))

            emit(
                SpiderManLocation(
                    latitude = currentLat,
                    longitude = currentLng,
                    timestamp = System.currentTimeMillis(),
                    activity = activity,
                    confidence = Random.nextInt(70, 100)
                )
            )

            delay(Random.nextLong(5000, 8000))
        }
    }
}
