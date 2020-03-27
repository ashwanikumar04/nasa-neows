package com.udacity.asteroidradar.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.getAsteroidsFromWeb
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates

class AsteroidDataDownloader(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val asteroidDao = AsteroidDatabase.getDatabase(context)
        .asteroidDao()

    override suspend fun doWork(): Result {
        return try {
            val sevenDays = getNextSevenDaysFormattedDates()
            val startDate = sevenDays[0]
            val endDate = sevenDays[6]
            val asteroids = getAsteroidsFromWeb(startDate, endDate)
            asteroidDao.deleteAll()
            asteroidDao.addAll(asteroids)
            Result.success()
        } catch (error: Throwable) {
            error.printStackTrace()
            Result.failure()
        }
    }
}