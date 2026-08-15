package com.spidertracker.app.presentation.tracker

import android.view.Gravity
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.spidertracker.app.domain.model.SpiderActivity
import com.spidertracker.app.domain.model.SpiderManLocation
import com.spidertracker.app.ui.components.RadarEffect
import com.spidertracker.app.ui.components.SpiderMarker
import com.spidertracker.app.ui.theme.SpiderRed
import com.spidertracker.app.util.DistanceFormatter
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style

@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }
    var maplibreMap by remember { mutableStateOf<MapLibreMap?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { mv ->
            mv.getMapAsync { map ->
                maplibreMap = map
                map.uiSettings.isAttributionEnabled = false
                map.uiSettings.isLogoEnabled = false
                map.setStyle("https://demotiles.maplibre.org/style.json") { _ ->
                    // Style loaded
                }
            }
        }

        // Overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🕷️ SPIDER-MAN TRACKER",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom UI: Distance and Sightings
            uiState.spiderManLocation?.let { _ ->
                SpiderStatusCard(
                    distanceMeters = uiState.distanceMeters,
                    activity = uiState.currentActivity ?: SpiderActivity.SWINGING
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SightingCardsList(uiState.sightings)
            }
        }

        // Animated Components (Markers etc)
        // Note: For a real app, these would be synchronized with the map projection
        // For this parody, we'll place them relative to the center or if maplibreMap is available
        maplibreMap?.let { map ->
            uiState.userLocation?.let { userLoc ->
                MapComposeOverlay(map, userLoc.latitude, userLoc.longitude) {
                    Box(contentAlignment = Alignment.Center) {
                        RadarEffect()
                        Text("📍", fontSize = 24.sp)
                    }
                }
            }
            
            uiState.spiderManLocation?.let { spiderLoc ->
                MapComposeOverlay(map, spiderLoc.latitude, spiderLoc.longitude) {
                    SpiderMarker(activity = spiderLoc.activity)
                }
            }

            // Web Trail (Dots/Icons)
            uiState.webTrail.drop(1).forEach { trailPoint ->
                MapComposeOverlay(map, trailPoint.latitude, trailPoint.longitude) {
                    Text("🕸️", modifier = Modifier.alpha(0.5f), fontSize = 16.sp)
                }
            }
        }
        
        // Nearby Event Alert
        if (uiState.showNearbyEvent) {
            NearbyAlert(onDismiss = { viewModel.dismissNearbyEvent() })
        }
    }

    // Camera movement
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let { loc ->
            maplibreMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(loc.latitude, loc.longitude))
                        .zoom(14.0)
                        .build()
                )
            )
        }
    }
}

@Composable
fun MapComposeOverlay(
    map: MapLibreMap,
    lat: Double,
    lng: Double,
    content: @Composable () -> Unit
) {
    val projection = map.projection
    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    val density = androidx.compose.ui.platform.LocalDensity.current
    
    LaunchedEffect(map, lat, lng) {
        while(true) {
            val point = projection.toScreenLocation(LatLng(lat, lng))
            offset = androidx.compose.ui.geometry.Offset(point.x, point.y)
            kotlinx.coroutines.delay(16)
        }
    }
    
    with(density) {
        Box(modifier = Modifier.offset(
            x = offset.x.toDp() - 24.dp, // Center the 48dp marker
            y = offset.y.toDp() - 24.dp
        )) {
            content()
        }
    }
}

@Composable
fun SpiderStatusCard(distanceMeters: Double, activity: SpiderActivity) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Spider-Man is ${DistanceFormatter.format(distanceMeters)} away",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SpiderRed
            )
            Text(
                text = "Activity: ${activity.title}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SightingCardsList(sightings: List<SpiderManLocation>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sightings) { sighting ->
            SightingCard(sighting)
        }
    }
}

@Composable
fun SightingCard(sighting: SpiderManLocation) {
    Surface(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "🕷️ ${sighting.activity.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "Conf: ${sighting.confidence}%", fontSize = 12.sp)
        }
    }
}

@Composable
fun NearbyAlert(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🕷️ SPIDER-MAN IS NEARBY!") },
        text = { Text("Look around! Possible rooftop activity detected in your immediate area.") },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = SpiderRed)) {
                Text("OK!")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = SpiderRed
    )
}
