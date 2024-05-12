package com.example.geomaps.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.geomaps.database.entity.Coordinate
import com.example.geomaps.database.entity.Track
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.mapview.MapView
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrackInfo(
    modifier: Modifier = Modifier,
    navController: NavController,
    track: Track?,
    coordinates: List<Coordinate>,
    onDeleteTrack: () -> Unit,
    onRenameTrack: (String) -> Unit,
) {
    track?.let {
        Scaffold(
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 0.dp, 20.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Button(modifier = Modifier
                        .fillMaxWidth(0.47f)
                        .height(50.dp)
                        .padding(15.dp, 0.dp, 0.dp, 0.dp),
                        onClick = { onRenameTrack("New track") }
                    ) {
                        Text("Переименовать")
                    }
                    Button(modifier = Modifier
                        .fillMaxWidth(0.89f)
                        .height(50.dp)
                        .padding(0.dp, 0.dp, 15.dp, 0.dp),
                        onClick = {
                            onDeleteTrack()
                            navController.popBackStack()
                        }) {
                        Text("Удалить")
                    }
                }
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp),
                        fontSize = 10.em,
                        text = track.name,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    val dist = calcTrackDistance(coordinates)
                    val distFormatted =
                        if (dist < 1) "%.2f м".format(dist * 1000) else "%.2f км".format(dist)

                    Text(
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp),
                        fontSize = 8.em,
                        text = "Пройденное расстояние: $distFormatted",
                    )
                }
                TrackMap(
                    modifier,
                    coordinates,
                )
            }
        }
    }
}

@Composable
fun TrackMap(
    modifier: Modifier,
    coordinates: List<Coordinate>,
) {
    Box(modifier = modifier) {
        AndroidView(
            factory = {
                val mv = MapView(it)

                mv.mapWindow.map.apply {
                    mapObjects.clear()

                    if (coordinates.isNotEmpty()) {
                        val pl = Polyline(coordinates.map { coord ->
                            Point(
                                coord.latitude,
                                coord.longitude
                            )
                        })

                        move(cameraPosition(calcCameraPosZoomed(pl.points)))

                        mapObjects.addPolyline(pl)
                    }
                }

                mv
            }
        )
    }
}

fun calcTrackDistance(coordinates: List<Coordinate>): Double {
    if (coordinates.size <= 1) {
        return 0.0
    }

    var res = 0.0
    for (i in 1..<coordinates.size) {
        val lat1 = coordinates[i - 1].latitude
        val lat2 = coordinates[i].latitude
        val lon1 = coordinates[i - 1].longitude
        val lon2 = coordinates[i].longitude

        res += 111.1 * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1))
    }

    return res
}
