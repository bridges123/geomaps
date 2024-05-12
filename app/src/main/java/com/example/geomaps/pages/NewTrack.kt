package com.example.geomaps.pages

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.geomaps.navigation.Page
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.mapview.MapView

const val mapZoom = 0.00003

enum class TrackState {
    START,
    PROGRESS,
}

var lastLocSize = 0

@Composable
fun NewTrack(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    locations: List<Location>,
    onClearLocations: () -> Unit,
    onAddCoordinates: (List<Point>) -> Unit,
    onStartTrack: (String, Point) -> Boolean,
    onFinishTrack: () -> Unit,
) {
    var trackState by remember { mutableStateOf(TrackState.START) }
    var trackName by remember { mutableStateOf("") }
    var openDialog by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Scaffold(modifier = modifier,
            bottomBar = {
                Footer(
                    navController = navController,
                    trackState = trackState,
                    onStartTrack = {
                        if (trackName.isEmpty()) {
                            openDialog = "Пустое название трека!"
                        } else {
                            onClearLocations()
                            lastLocSize = locations.size

                            val ok = onStartTrack(
                                trackName,
                                Point(locations[0].latitude, locations[0].longitude)
                            )
                            if (!ok) {
                                openDialog = "Категория с таким названием уже есть!"
                            } else {
                                trackState = TrackState.PROGRESS
                            }
                        }
                    },
                    onFinishTrack = onFinishTrack,
                )
            }) {
            Column(
                modifier = Modifier.padding(it),
            ) {
                NewTrackHeader(trackName) { trackName = it }

                NewTrackMap(
                    modifier,
                    locations,
                    trackState,
                    onAddCoordinates,
                )
            }
        }
    }
    if (openDialog.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { openDialog = "" },
            title = { Text(text = "Ошибка") },
            text = { Text(openDialog) },
            confirmButton = {
                Button({ openDialog = "" }) {
                    Text("OK", fontSize = 5.em)
                }
            }
        )
    }
}

@Composable
fun NewTrackHeader(
    trackName: String,
    onChangeName: (String) -> Unit,
) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Новый трек",
                fontSize = 11.em,
                modifier = Modifier
                    .padding(0.dp, 15.dp)
            )
        }
        Row(
            Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = trackName,
                onValueChange = { onChangeName(it) },
                placeholder = { Text("Название") },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 0.dp, 0.dp, 20.dp)
            )
        }
    }
}

@Composable
fun NewTrackMap(
    modifier: Modifier,
    locations: List<Location>,
    trackState: TrackState,
    onAddCoordinates: (List<Point>) -> Unit,
) {
    Box(modifier = modifier) {
        AndroidView(
            factory = {
                MapView(it)
            },
            update = { mapView ->
                mapView.mapWindow.map.apply {
                    if (locations.isNotEmpty()) {
                        val pl = Polyline(locations.map { loc ->
                            Point(
                                loc.latitude,
                                loc.longitude
                            )
                        })

                        move(
                            cameraPosition(calcCameraPosZoomed(pl.points)),
                            Animation(Animation.Type.SMOOTH, 1f),
                            null
                        )

                        if (trackState == TrackState.PROGRESS) {
                            mapObjects.addPolyline(pl)
                            onAddCoordinates(pl.points.subList(lastLocSize, locations.size))
                        }

                        lastLocSize = locations.size
                    }
                }
            }
        )
    }
}

@Composable
fun Footer(
    navController: NavHostController,
    trackState: TrackState,
    onStartTrack: () -> Unit,
    onFinishTrack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if (trackState == TrackState.PROGRESS) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(60.dp),
                onClick = {
                    onFinishTrack()
                    navController.navigate(Page.TRACK_INFO.route) {
                        popUpTo(Page.MAIN.route)
                    }
                }
            ) {
                Text("Стоп", fontSize = 7.em)
            }
        } else {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(60.dp),
                onClick = onStartTrack
            ) {
                Text("Старт", fontSize = 7.em)
            }
        }
    }
}

fun calcCameraPosZoomed(points: List<Point>): Geometry {
    var top = Point(0.0, 0.0)
    var right = Point(0.0, 0.0)
    var bot = Point(1000.0, 0.0)
    var left = Point(0.0, 1000.0)

    for (point in points) {
        if (point.latitude > top.latitude) {
            top = point
        }
        if (point.longitude > right.longitude) {
            right = point
        }
        if (point.latitude < bot.latitude) {
            bot = point
        }
        if (point.longitude < left.longitude) {
            left = point
        }
    }

    top = Point((1 + mapZoom) * top.latitude, top.longitude)
    right = Point(right.latitude, (1 + mapZoom) * right.longitude)
    bot = Point((1 - mapZoom) * bot.latitude, bot.longitude)
    left = Point(left.latitude, (1 - mapZoom) * left.longitude)

    return Geometry.fromPolyline(Polyline(listOf(top, right, bot, left)))
}
