package com.udacity.asteroidradar.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {
    @GET("/neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Response<String>
}