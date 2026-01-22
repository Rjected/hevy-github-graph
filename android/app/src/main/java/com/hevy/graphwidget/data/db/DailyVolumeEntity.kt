package com.hevy.graphwidget.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_volumes")
data class DailyVolumeEntity(
    @PrimaryKey
    val date: String,  // Format: "2026-01-21"
    val volumeKg: Double
)
