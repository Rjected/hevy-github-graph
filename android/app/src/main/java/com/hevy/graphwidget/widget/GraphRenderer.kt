package com.hevy.graphwidget.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import java.time.LocalDate

class GraphRenderer(
    private val theme: ColorTheme = ColorTheme.BLUE
) {
    companion object {
        private const val WEEKS = 15
        private const val DAYS = 7
        private const val CELL_SIZE = 58f
        private const val CELL_GAP = 8f
        private const val CORNER_RADIUS = 6f
    }
    
    fun render(dailyVolumes: Map<LocalDate, Double>, widthPx: Int? = null): Bitmap {
        val cellSize = CELL_SIZE
        val gap = CELL_GAP
        
        // Calculate grid dimensions (no extra padding - let widget handle it)
        val gridWidth = (cellSize * WEEKS + gap * (WEEKS - 1)).toInt()
        val gridHeight = (cellSize * DAYS + gap * (DAYS - 1)).toInt()
        
        val bitmap = Bitmap.createBitmap(gridWidth, gridHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        // Build date grid (WEEKS weeks x 7 days, ending today)
        val today = LocalDate.now()
        val todayDow = today.dayOfWeek.value % 7  // Sunday = 0
        val daysToShow = (WEEKS - 1) * 7 + todayDow + 1
        val startDate = today.minusDays(daysToShow.toLong() - 1)
        
        // Collect volumes for normalization
        val volumes = mutableListOf<Double>()
        
        for (dayOffset in 0 until daysToShow) {
            val date = startDate.plusDays(dayOffset.toLong())
            val col = dayOffset / 7
            val row = (date.dayOfWeek.value % 7)
            if (col < WEEKS && row < DAYS) {
                volumes.add(dailyVolumes[date] ?: 0.0)
            }
        }
        
        // Compute intensity levels (quantile-based)
        val levels = computeIntensityLevels(volumes)
        
        // Draw cells
        var volumeIndex = 0
        for (dayOffset in 0 until daysToShow) {
            val date = startDate.plusDays(dayOffset.toLong())
            val col = dayOffset / 7
            val row = (date.dayOfWeek.value % 7)
            
            if (col < WEEKS && row < DAYS) {
                val level = levels.getOrElse(volumeIndex) { 0 }
                volumeIndex++
                
                val x = col * (cellSize + gap)
                val y = row * (cellSize + gap)
                
                paint.color = theme.colors[level]
                canvas.drawRoundRect(
                    RectF(x, y, x + cellSize, y + cellSize),
                    CORNER_RADIUS, CORNER_RADIUS,
                    paint
                )
            }
        }
        
        return bitmap
    }
    
    private fun computeIntensityLevels(volumes: List<Double>): List<Int> {
        val nonZero = volumes.filter { it > 0 }.sorted()
        if (nonZero.isEmpty()) {
            return volumes.map { 0 }
        }
        
        val q1 = nonZero.getOrElse((nonZero.size * 0.25).toInt()) { nonZero.first() }
        val q2 = nonZero.getOrElse((nonZero.size * 0.50).toInt()) { nonZero.first() }
        val q3 = nonZero.getOrElse((nonZero.size * 0.75).toInt()) { nonZero.first() }
        
        return volumes.map { v ->
            when {
                v <= 0 -> 0
                v <= q1 -> 1
                v <= q2 -> 2
                v <= q3 -> 3
                else -> 4
            }
        }
    }
}
