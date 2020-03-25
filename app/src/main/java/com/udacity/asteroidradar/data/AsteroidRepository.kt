package com.udacity.asteroidradar.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.TodayImage
import com.udacity.asteroidradar.api.AsteroidService
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class AsteroidRepository(val app: Application) {
    val todayImage = MutableLiveData<TodayImage>()
    val asteroidData = MutableLiveData<List<Asteroid>>()
    private val asteroidDao = AsteroidDatabase.getDatabase(app)
        .asteroidDao()
    private var startDate: String
    private var endDate: String

    init {
        val sevenDays = getNextSevenDaysFormattedDates()
        startDate = sevenDays[0]
        endDate = sevenDays[6]
        todayImage.value = TodayImage("", "", "")
        CoroutineScope(Dispatchers.IO).launch {

            val data = asteroidDao.getAll(startDate)
            if (data.isEmpty()) {
                getAsteroids(startDate, endDate)
            } else {
                asteroidData.postValue(data)
            }
            getTodayImage()
        }
    }

    @WorkerThread
    suspend fun getAsteroids(startDate: String, endDate: String) {
        if (networkAvailable()) {
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

    @WorkerThread
    suspend fun getTodayImage() {
        if (networkAvailable()) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            val service = retrofit.create(AsteroidService::class.java)
            val serviceData = service.getTodayImage(
                API_KEY
            ).body()
            todayImage.postValue(serviceData)
        }
    }

    @Suppress("DEPRECATION")
    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }

    fun fetchTodayAsteroids() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = asteroidDao.getByDate(startDate)
            asteroidData.postValue(data)
        }
    }

    fun fetchAll() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = asteroidDao.getAll(startDate)
            asteroidData.postValue(data)
        }
    }

    fun fetchCurrentWeek() {
        CoroutineScope(Dispatchers.IO).launch {
            val weekend = getWeekEnd();

            val data = asteroidDao.getByRange(startDate, weekend ?: startDate)
            asteroidData.postValue(data)
        }
    }

    private fun getWeekEnd(): String? {
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days[i] = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days[6]
    }
}