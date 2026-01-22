package com.hevy.graphwidget.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hevy.graphwidget.R
import com.hevy.graphwidget.data.repo.WorkoutsRepository
import com.hevy.graphwidget.widget.ColorTheme
import com.hevy.graphwidget.widget.HevyGraphWidgetProvider
import com.hevy.graphwidget.widget.WidgetPreferences
import com.hevy.graphwidget.work.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WidgetConfigActivity : AppCompatActivity() {
    
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var prefs: WidgetPreferences
    
    private lateinit var apiKeyInput: EditText
    private lateinit var colorSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)
        
        // Set result to CANCELED in case user backs out
        setResult(RESULT_CANCELED)
        
        // Get the widget ID from the intent
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        
        prefs = WidgetPreferences(this)
        
        // Initialize views
        apiKeyInput = findViewById(R.id.api_key_input)
        colorSpinner = findViewById(R.id.color_spinner)
        saveButton = findViewById(R.id.save_button)
        progressBar = findViewById(R.id.progress_bar)
        statusText = findViewById(R.id.status_text)
        
        // Load existing values
        prefs.getApiKey()?.let { apiKeyInput.setText(it) }
        
        // Setup color spinner
        val colorNames = ColorTheme.entries.map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colorNames)
        colorSpinner.adapter = adapter
        
        val currentTheme = ColorTheme.fromName(prefs.getColorTheme())
        colorSpinner.setSelection(ColorTheme.entries.indexOf(currentTheme))
        
        // Save button
        saveButton.setOnClickListener {
            saveAndSync()
        }
    }
    
    private fun saveAndSync() {
        val apiKey = apiKeyInput.text.toString().trim()
        if (apiKey.isEmpty()) {
            statusText.text = "Please enter your API key"
            return
        }
        
        val selectedTheme = ColorTheme.entries[colorSpinner.selectedItemPosition]
        
        // Save preferences
        prefs.setApiKey(apiKey)
        prefs.setColorTheme(selectedTheme.name)
        
        // Show progress
        saveButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        statusText.text = "Syncing workouts..."
        
        // Sync data
        CoroutineScope(Dispatchers.IO).launch {
            val repo = WorkoutsRepository(this@WidgetConfigActivity)
            val result = repo.syncWorkouts(apiKey)
            
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                
                if (result.isSuccess) {
                    val count = result.getOrDefault(0)
                    statusText.text = "Synced $count workouts!"
                    
                    // Schedule periodic sync
                    SyncWorker.schedulePeriodic(this@WidgetConfigActivity)
                    
                    // Update widget
                    val appWidgetManager = AppWidgetManager.getInstance(this@WidgetConfigActivity)
                    HevyGraphWidgetProvider.updateWidget(
                        this@WidgetConfigActivity,
                        appWidgetManager,
                        appWidgetId
                    )
                    
                    // Return success
                    val resultValue = Intent().apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    setResult(RESULT_OK, resultValue)
                    finish()
                } else {
                    saveButton.isEnabled = true
                    statusText.text = "Sync failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
                }
            }
        }
    }
}
