package com.udacity.asteroidradar.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.data.AsteroidRepository

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    private val dataRepo = AsteroidRepository(app)
    val asteroidData = dataRepo.asteroidData
    val todayImage = dataRepo.todayImage

    val selectedAsteroid = MutableLiveData<Asteroid>()

    fun fetchTodayAsteroids() {
        dataRepo.fetchTodayAsteroids()
    }

    fun fetchAll() {
        dataRepo.fetchAll()
    }

    fun fetchCurrentWeek() {
        dataRepo.fetchCurrentWeek()
    }
}