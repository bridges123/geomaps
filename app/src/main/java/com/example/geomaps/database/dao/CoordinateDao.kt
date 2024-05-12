package com.example.geomaps.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.geomaps.database.entity.Coordinate
import com.example.geomaps.database.entity.Track
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface CoordinateDao {

    @Query("select * from coordinate where track_id = :trackId")
    fun getByTrack(trackId: Long): Flow<List<Coordinate>>

    @Query("insert into coordinate (track_id, latitude, longitude, datetime) values (:trackId, :latitude, :longitude, :datetime)")
    fun insert(trackId: Long, latitude: Double, longitude: Double, datetime: LocalDateTime): Long

    @Insert
    fun insertAll(vararg coordinates: Coordinate)

    @Delete
    fun delete(category: Track)
}