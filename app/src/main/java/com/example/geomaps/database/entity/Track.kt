package com.example.geomaps.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "track",
    indices = [Index("id")]
)
data class Track(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "start_dt") var startDt: LocalDateTime,
    @ColumnInfo(name = "finish_dt") var finishDt: LocalDateTime,
)
