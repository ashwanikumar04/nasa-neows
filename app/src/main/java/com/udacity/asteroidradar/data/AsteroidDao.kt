package com.udacity.asteroidradar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {

    @Query("SELECT * from asteroid")
    fun getAll(): List<Asteroid>

    @Insert
    suspend fun add(asteroid: Asteroid)

    @Insert
    suspend fun addAll(asteroids: List<Asteroid>)

    @Query("DELETE from asteroid")
    suspend fun deleteAll()

}