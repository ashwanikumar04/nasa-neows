package com.udacity.asteroidradar.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.getAsteroidsFromWeb
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.data.MoshiUtil.Companion.serialize

class AsteroidDataDownloader(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val asteroidDao = AsteroidDatabase.getDatabase(context)
        .asteroidDao()

    override suspend fun doWork(): Result {
        return try {
            Log.d("Test", "Came here1")
            val sevenDays = getNextSevenDaysFormattedDates()
            val startDate = sevenDays[0]
            val endDate = sevenDays[6]
            val asteroids = getAsteroidsFromWeb(startDate, endDate)
            asteroidDao.deleteAll()
            asteroidDao.addAll(asteroids)
            Result.success()
        } catch (error: Throwable) {
            Log.e("Test", "Came here2")
            error.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val WORK_RESULT = "work_result"
    }
}