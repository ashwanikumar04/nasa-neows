package com.udacity.asteroidradar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
/// Reference https://androidkt.com/datetime-datatype-sqlite-using-room/
interface AsteroidDao {

    @Query("SELECT * from asteroid where date(closeApproachDate) >= date(:startDate) ORDER BY date(closeApproachDate) asc")
    fun getAll(startDate: String): List<Asteroid>

    @Insert
    suspend fun add(asteroid: Asteroid)

    @Insert
    suspend fun addAll(asteroids: List<Asteroid>)

    @Query("DELETE from asteroid")
    suspend fun deleteAll()

}