package com.example.geomaps.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.geomaps.database.dao.CoordinateDao
import com.example.geomaps.database.dao.TrackDao
import com.example.geomaps.database.entity.Coordinate
import com.example.geomaps.database.entity.Track
import com.example.geomaps.util.LocalDateTimeConverter

@Database(
    version = 1,
    entities = [
        Track::class,
        Coordinate::class,
    ]
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTrackDao(): TrackDao
    abstract fun getCoordinateDao(): CoordinateDao
}