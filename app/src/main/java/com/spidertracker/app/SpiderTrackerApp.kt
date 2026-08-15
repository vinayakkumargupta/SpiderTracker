package com.spidertracker.app

import android.app.Application
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class SpiderTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize MapLibre
        MapLibre.getInstance(this)
    }
}
