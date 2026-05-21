package com.example.ripkei.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ripkei.ui.MainViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@Composable
fun MapScreen(viewModel: MainViewModel) {
    val logs by viewModel.allLogs.collectAsState()
    
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 2f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapMapProperties(
            isMyLocationEnabled = false,
            // You can add a dark mode map style here
            // mapStyleOptions = MapStyleOptions(jsonStyle)
        ),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        logs.forEach { log ->
            if (log.latitude != null && log.longitude != null) {
                Marker(
                    state = MarkerState(position = LatLng(log.latitude, log.longitude)),
                    title = log.destinationIp,
                    snippet = log.appName ?: log.packageName
                )
            }
        }
    }
}
