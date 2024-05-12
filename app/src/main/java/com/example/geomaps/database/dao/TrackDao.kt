package com.example.geomaps.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.geomaps.database.entity.Track
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TrackDao {

    @Query("select * from track")
    fun getAll(): Flow<List<Track>>

    @Query("select * from track where name = :name limit 1")
    fun getByName(name: String): Flow<Track>

    @Query("insert into track (name, start_dt, finish_dt) values (:name, :startDt, 0)")
    fun insert(name: String, startDt: LocalDateTime): Long

    @Query("update track set name = :name where id = :trackId")
    fun rename(trackId: Long, name: String)

    @Query("update track set finish_dt = :finishDt where id = :trackId")
    fun finish(trackId: Long, finishDt: LocalDateTime)

    @Delete
    fun delete(track: Track)
}