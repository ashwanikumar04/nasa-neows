package com.udacity.asteroidradar.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.api.AsteroidService
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class AsteroidRepository(val app: Application) {

    val asteroidData = MutableLiveData<List<Asteroid>>()
    private val asteroidDao = AsteroidDatabase.getDatabase(app)
        .asteroidDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val sevenDays = getNextSevenDaysFormattedDates()
            val startDate = sevenDays[0]
            val endDate = sevenDays[6]

            val data = asteroidDao.getAll(startDate)
            if (data.isEmpty()) {
                callWebService(startDate, endDate)
            } else {
                asteroidData.postValue(data)
            }
        }
    }

    @WorkerThread
    suspend fun callWebService(startDate: String, endDate: String) {
        if (networkAvailable()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "Using remote data", Toast.LENGTH_LONG).show()
            }
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
            val service = retrofit.create(AsteroidService::class.java)
            val serviceData = service.getAsteroids(
                startDate
                , endDate
                , API_KEY
            ).body()

            val asteroids: List<Asteroid> = if (serviceData != null) {
                parseAsteroidsJsonResult(JSONObject(serviceData))
            } else
                emptyList()
            asteroidData.postValue(asteroids)
            asteroidDao.deleteAll()
            asteroidDao.addAll(asteroids)
        }
    }

    @Suppress("DEPRECATION")
    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }

    fun refreshDataFromWeb() {
        CoroutineScope(Dispatchers.IO).launch {
            // callWebService(startDate, endDate)
        }
    }
}