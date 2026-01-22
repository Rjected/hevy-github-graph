package com.hevy.graphwidget.data.repo

import android.content.Context
import com.hevy.graphwidget.data.api.HevyClient
import com.hevy.graphwidget.data.api.Workout
import com.hevy.graphwidget.data.db.AppDatabase
import com.hevy.graphwidget.data.db.DailyVolumeEntity
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class WorkoutsRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).dailyVolumeDao()
    private val api = HevyClient.api
    
    suspend fun syncWorkouts(apiKey: String): Result<Int> {
        return try {
            val allWorkouts = mutableListOf<Workout>()
            var page = 1
            var pageCount = 1
            
            // Fetch all pages
            while (page <= pageCount) {
                val response = api.getWorkouts(apiKey, page)
                allWorkouts.addAll(response.workouts)
                pageCount = response.pageCount
                page++
            }
            
            // Aggregate daily volumes
            val dailyVolumes = aggregateDailyVolumes(allWorkouts)
            
            // Save to database
            dao.deleteAll()
            dao.insertAll(dailyVolumes)
            
            Result.success(allWorkouts.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun aggregateDailyVolumes(workouts: List<Workout>): List<DailyVolumeEntity> {
        val volumeByDate = mutableMapOf<String, Double>()
        
        for (workout in workouts) {
            val date = try {
                OffsetDateTime.parse(workout.startTime).toLocalDate().toString()
            } catch (e: Exception) {
                continue
            }
            
            var workoutVolume = 0.0
            for (exercise in workout.exercises) {
                for (set in exercise.sets) {
                    val weight = set.weightKg ?: 0.0
                    val reps = set.reps ?: 0
                    workoutVolume += weight * reps
                }
            }
            
            volumeByDate[date] = (volumeByDate[date] ?: 0.0) + workoutVolume
        }
        
        return volumeByDate.map { (date, volume) ->
            DailyVolumeEntity(date, volume)
        }
    }
    
    suspend fun getDailyVolumes(days: Int = 365): Map<LocalDate, Double> {
        val startDate = LocalDate.now().minusDays(days.toLong()).toString()
        val entities = dao.getFromDate(startDate)
        return entities.associate { 
            LocalDate.parse(it.date) to it.volumeKg 
        }
    }
}
