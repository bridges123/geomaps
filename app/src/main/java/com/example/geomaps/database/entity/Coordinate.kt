package com.example.geomaps.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "coordinate",
    indices = [Index("id")],
    foreignKeys = [ForeignKey(
        entity = Track::class,
        parentColumns = ["id"],
        childColumns = ["track_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Coordinate(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "track_id") var trackId: Long,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double,
    @ColumnInfo(name = "datetime") var datetime: LocalDateTime,
)
