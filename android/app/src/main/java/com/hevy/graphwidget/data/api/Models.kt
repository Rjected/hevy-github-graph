package com.hevy.graphwidget.data.api

import com.google.gson.annotations.SerializedName

data class WorkoutsResponse(
    val page: Int,
    @SerializedName("page_count") val pageCount: Int,
    val workouts: List<Workout>
)

data class Workout(
    val id: String,
    val title: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val exercises: List<Exercise>
)

data class Exercise(
    val title: String,
    @SerializedName("exercise_template_id") val exerciseTemplateId: String,
    val sets: List<ExerciseSet>
)

data class ExerciseSet(
    val type: String,
    @SerializedName("weight_kg") val weightKg: Double?,
    val reps: Int?,
    @SerializedName("distance_meters") val distanceMeters: Double?,
    @SerializedName("duration_seconds") val durationSeconds: Int?,
    val rpe: Double?
)
