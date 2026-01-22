package com.hevy.graphwidget.widget

import android.graphics.Color

enum class ColorTheme(val displayName: String, val colors: IntArray) {
    BLUE("Blue", intArrayOf(
        Color.rgb(35, 38, 45),      // 0: darker gray (no workout)
        Color.rgb(140, 180, 255),   // 1: lightest blue (light workout)
        Color.rgb(80, 130, 220),    // 2
        Color.rgb(40, 90, 180),     // 3
        Color.rgb(15, 50, 120)      // 4: darkest blue (heavy workout)
    )),
    GREEN("Green", intArrayOf(
        Color.rgb(35, 38, 45),
        Color.rgb(140, 230, 160),   // lightest green
        Color.rgb(80, 180, 100),
        Color.rgb(40, 130, 60),
        Color.rgb(15, 80, 30)       // darkest green
    )),
    PURPLE("Purple", intArrayOf(
        Color.rgb(35, 38, 45),
        Color.rgb(200, 160, 255),   // lightest purple
        Color.rgb(160, 110, 220),
        Color.rgb(120, 70, 180),
        Color.rgb(70, 30, 120)      // darkest purple
    )),
    ORANGE("Orange", intArrayOf(
        Color.rgb(35, 38, 45),
        Color.rgb(255, 200, 140),   // lightest orange
        Color.rgb(230, 150, 80),
        Color.rgb(200, 100, 40),
        Color.rgb(140, 60, 15)      // darkest orange
    ));
    
    companion object {
        fun fromName(name: String): ColorTheme {
            return entries.find { it.name == name } ?: BLUE
        }
    }
}
