package com.hevy.graphwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.hevy.graphwidget.R
import com.hevy.graphwidget.data.repo.WorkoutsRepository
import com.hevy.graphwidget.work.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HevyGraphWidgetProvider : AppWidgetProvider() {
    
    companion object {
        const val ACTION_REFRESH = "com.hevy.graphwidget.ACTION_REFRESH"
        
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = WidgetPreferences(context)
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            
            // Make whole widget clickable to refresh
            val refreshIntent = Intent(context, HevyGraphWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val refreshPending = PendingIntent.getBroadcast(
                context, appWidgetId, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, refreshPending)
            
            // Load and render graph
            CoroutineScope(Dispatchers.IO).launch {
                val repo = WorkoutsRepository(context)
                val volumes = repo.getDailyVolumes(365)
                val theme = ColorTheme.fromName(prefs.getColorTheme())
                val renderer = GraphRenderer(theme)
                val bitmap = renderer.render(volumes)
                
                views.setImageViewBitmap(R.id.graph_image, bitmap)
                
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
    
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action == ACTION_REFRESH) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                // Trigger sync
                SyncWorker.enqueueOneTime(context)
            }
        }
    }
    
    override fun onEnabled(context: Context) {
        // Schedule periodic sync when first widget is added
        SyncWorker.schedulePeriodic(context)
    }
    
    override fun onDisabled(context: Context) {
        // Cancel periodic sync when last widget is removed
        SyncWorker.cancel(context)
    }
}
