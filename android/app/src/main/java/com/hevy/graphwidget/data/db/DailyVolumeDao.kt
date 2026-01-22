package com.hevy.graphwidget.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyVolumeDao {
    @Query("SELECT * FROM daily_volumes ORDER BY date DESC")
    suspend fun getAll(): List<DailyVolumeEntity>
    
    @Query("SELECT * FROM daily_volumes WHERE date >= :startDate ORDER BY date ASC")
    suspend fun getFromDate(startDate: String): List<DailyVolumeEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(volumes: List<DailyVolumeEntity>)
    
    @Query("DELETE FROM daily_volumes")
    suspend fun deleteAll()
}
