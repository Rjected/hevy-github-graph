package com.hevy.graphwidget.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HevyApi {
    @GET("v1/workouts")
    suspend fun getWorkouts(
        @Header("api-key") apiKey: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 10
    ): WorkoutsResponse
}
