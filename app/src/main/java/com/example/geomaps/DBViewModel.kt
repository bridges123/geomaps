package com.example.geomaps

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.geomaps.database.AppDatabase
import com.example.geomaps.database.entity.Coordinate
import com.example.geomaps.database.entity.Track
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class DBViewModel(app: Application) : AndroidViewModel(app) {
    private val db =
        Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "geo_maps").build()

    private val trackDao = db.getTrackDao()
    private val coordinateDao = db.getCoordinateDao()

    private var coordinatesJob: Job? = null

    var tracks by mutableStateOf(listOf<Track>())
    var trackCoordinates by mutableStateOf(listOf<Coordinate>())

    var selectedTrack by mutableStateOf<Track?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            trackDao.getAll().collect { trackList ->
                withContext(Dispatchers.Main) {
                    tracks = trackList
                }
            }
        }
    }

    fun selectTrack(track: Track) {
        selectedTrack = track
        getTrackCoordinates()
    }

    private fun getTrackCoordinates() {
        selectedTrack?.let { track ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    coordinatesJob?.cancelAndJoin()
                } catch (_: Throwable) {

                }

                coordinatesJob = launch {
                    coordinateDao.getByTrack(track.id).collect { coords ->
                        trackCoordinates = coords
                    }
                }
            }
        }
    }

    fun startTrack(trackName: String, startPoint: Point): Boolean {
        if (tracks.any { it.name == trackName }) {
            return false
        }

        viewModelScope.launch(Dispatchers.IO) {
            trackDao.insert(trackName, LocalDateTime.now())
            selectTrack(trackDao.getByName(trackName).first())
            selectedTrack!!.let { track ->
                coordinateDao.insert(
                    track.id,
                    startPoint.latitude,
                    startPoint.longitude,
                    track.startDt
                )
            }
        }
        return true
    }

    fun renameTrack(name: String) {
        selectedTrack?.let { track ->
            viewModelScope.launch(Dispatchers.IO) {
                trackDao.rename(track.id, name)
            }
        }
    }

    fun finishTrack() {
        selectedTrack?.let { track ->
            viewModelScope.launch(Dispatchers.IO) {
                trackDao.finish(track.id, LocalDateTime.now())
            }
        }
    }

    fun addTrackCoordinates(points: List<Point>) {
        selectedTrack?.let { track ->
            viewModelScope.launch(Dispatchers.IO) {
                coordinateDao.insertAll(*points.map {
                    Coordinate(
                        0,
                        track.id,
                        it.latitude,
                        it.longitude,
                        LocalDateTime.now()
                    )
                }.toTypedArray())
            }
        }
    }

    fun deleteTrack() {
        selectedTrack?.let { track ->
            viewModelScope.launch(Dispatchers.IO) {
                trackDao.delete(track)
            }
        }
        selectedTrack = null
    }
}