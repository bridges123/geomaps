package com.example.geomaps.navigation

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.geomaps.database.entity.Coordinate
import com.example.geomaps.database.entity.Track
import com.example.geomaps.pages.Main
import com.example.geomaps.pages.NewTrack
import com.example.geomaps.pages.TrackInfo
import com.yandex.mapkit.geometry.Point

@Composable
fun NavContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    locations: List<Location>,
    onClearLocations: () -> Unit,
    tracks: List<Track>,
    onSelectTrack: (Track) -> Unit,
    selectedTrack: Track?,
    trackCoordinates: List<Coordinate>,
    onStartTrack: (String, Point) -> Boolean,
    onFinishTrack: () -> Unit,
    onDeleteTrack: () -> Unit,
    onRenameTrack: (String) -> Unit,
    onAddCoordinates: (List<Point>) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Page.MAIN.route,
        modifier = modifier,
    ) {
        composable(Page.MAIN.route) {
            Main(
                Modifier.fillMaxSize(),
                navController,
                tracks,
                onSelectTrack,
            )
        }
        composable(Page.NEW_TRACK.route) {
            NewTrack(
                Modifier.fillMaxSize(),
                navController,
                locations,
                onClearLocations,
                onAddCoordinates,
                onStartTrack,
                onFinishTrack,
            )
        }
        composable(Page.TRACK_INFO.route) {
            TrackInfo(
                Modifier.fillMaxSize(),
                navController,
                selectedTrack,
                trackCoordinates,
                onDeleteTrack,
                onRenameTrack,
            )
        }
    }
}