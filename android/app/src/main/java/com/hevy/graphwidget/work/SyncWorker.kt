package com.hevy.graphwidget.work

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.*
import com.hevy.graphwidget.data.repo.WorkoutsRepository
import com.hevy.graphwidget.widget.HevyGraphWidgetProvider
import com.hevy.graphwidget.widget.WidgetPreferences
import java.util.concurrent.TimeUnit

class SyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val WORK_NAME_PERIODIC = "hevy_sync_periodic"
        private const val WORK_NAME_ONE_TIME = "hevy_sync_one_time"
        
        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                24, TimeUnit.HOURS,
                1, TimeUnit.HOURS  // Flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        fun enqueueOneTime(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
        
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_PERIODIC)
        }
    }
    
    override suspend fun doWork(): Result {
        val prefs = WidgetPreferences(context)
        val apiKey = prefs.getApiKey() ?: return Result.failure()
        
        val repo = WorkoutsRepository(context)
        val result = repo.syncWorkouts(apiKey)
        
        return if (result.isSuccess) {
            // Update all widgets
            updateAllWidgets()
            Result.success()
        } else {
            Result.retry()
        }
    }
    
    private fun updateAllWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, HevyGraphWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            HevyGraphWidgetProvider.updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
