package com.example.geomaps

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.geomaps.navigation.NavContent
import com.example.geomaps.ui.theme.GeoMapsTheme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {

    private lateinit var requester: ActivityResultLauncher<Array<String>>
    private val locMVM by viewModels<LocationViewModel>()
    private val dbMVM by viewModels<DBViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f933b9d6-944c-4c3a-8714-d506cd72a3c9")
        MapKitFactory.initialize(this)

        requester = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (!it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                locMVM.startLocationUpdates()
            }
        }

        requester.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        setContent {
            val navController: NavHostController = rememberNavController()
            GeoMapsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavContent(
                        navController = navController,
                        locations = locMVM.locations,
                        onClearLocations = locMVM::clearLocations,
                        tracks = dbMVM.tracks,
                        onSelectTrack = dbMVM::selectTrack,
                        selectedTrack = dbMVM.selectedTrack,
                        trackCoordinates = dbMVM.trackCoordinates,
                        onStartTrack = dbMVM::startTrack,
                        onFinishTrack = dbMVM::finishTrack,
                        onDeleteTrack = dbMVM::deleteTrack,
                        onRenameTrack = dbMVM::renameTrack,
                        onAddCoordinates = dbMVM::addTrackCoordinates,
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
