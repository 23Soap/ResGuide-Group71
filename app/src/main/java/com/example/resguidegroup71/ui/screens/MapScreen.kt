package com.example.resguidegroup71.ui.screens

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resguidegroup71.viewmodel.RestaurantViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    viewModel: RestaurantViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Initialize OSM configuration
    LaunchedEffect(Unit) {
        @Suppress("DEPRECATION")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        Configuration.getInstance().load(context, sharedPreferences)
        Configuration.getInstance().userAgentValue = "MyOwnAppXYZ1234/1.0" // Different agent
    }

    val restaurantList by viewModel.allRestaurants.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setMultiTouchControls(true)

                        setTileSource(TileSourceFactory.OpenTopo) 
                        
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(43.651070, -79.347015)) // Toronto
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    
                    var firstValidPoint: GeoPoint? = null

                    restaurantList.forEach { restaurant ->
                        if (restaurant.latitude != null && restaurant.longitude != null) {
                            val point = GeoPoint(restaurant.latitude, restaurant.longitude)
                            if (firstValidPoint == null) firstValidPoint = point

                            val marker = Marker(mapView)
                            marker.position = point
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.title = restaurant.name
                            mapView.overlays.add(marker)
                        }
                    }

                    if (firstValidPoint != null) {
                        mapView.controller.setCenter(firstValidPoint)
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
