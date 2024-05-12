package com.example.geomaps

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomaps.locating.Locator
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationViewModel(app: Application) : AndroidViewModel(app) {
    private val fusedLocationClient = LocationServices
        .getFusedLocationProviderClient(app.applicationContext)

    val locations = mutableStateListOf<Location>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Locator.location.collect { loc ->
                withContext(Dispatchers.Main) {
                    loc?.let {
                        locations.add(it)
                    }
                }
            }
        }
    }

    fun clearLocations() {
        locations.removeRange(0, locations.size-1)
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener {
            viewModelScope.launch {
                fusedLocationClient.requestLocationUpdates(
                    Locator.locationRequest,
                    Locator,
                    Looper.getMainLooper()
                )
            }
        }

    }

}