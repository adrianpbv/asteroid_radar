package com.udacity.asteroidradar.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.udacity.asteroidradar.api.getDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.database.AsteroidsDataBase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.asImageDomain
import com.udacity.asteroidradar.main.AsteroidFilter
import com.udacity.asteroidradar.network.AsteroidsApi
import com.udacity.asteroidradar.network.NetworkData
import com.udacity.asteroidradar.network.asDataBaseModel
import com.udacity.asteroidradar.ui.Asteroid
import com.udacity.asteroidradar.ui.NasaImage
import com.udacity.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.UnknownHostException
import java.util.*

/**
 * Repository that communicates with the NetworkApi and the database
 */
class Repository(private val database: AsteroidsDataBase) {
    private var START_DATE: String
    private var END_DATE: String

    init {
        val calendar = Calendar.getInstance()
        START_DATE = getDay(calendar.time)
        calendar.add(Calendar.DAY_OF_WEEK, 7) // one week ahead
        END_DATE = getDay(calendar.time)
    }

    // LiveData to get the Nasa's Image
    val pictureOfDay: LiveData<NasaImage> = Transformations.map(
        database.asteroidDao.getPictureOfDay()
    ) {
        asImageDomain(it)
    }

    /**
     * Refresh the asteroids in the dataBase with data from the network
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroids = parseAsteroidsJsonResult(
                JSONObject(
                    AsteroidsApi.retrofitService.getAsteroids(
                        START_DATE, END_DATE, Constants.API_KEY
                    )
                )
            )
            val networkData = NetworkData(asteroids)
            database.asteroidDao.insertAll(*networkData.asDataBaseModel())
        }
    }

    /**
     * Loading the Nasa Day Image
     */
    suspend fun refreshImage() {
        withContext(Dispatchers.IO) {
            val img = AsteroidsApi.retrofitService.getNasaDayImage(Constants.API_KEY, true)

            // Insert in the DataBase the nasa's day picture
            database.asteroidDao.insertPicture(img.toImageDatabase())
        }
    }

    /**
     *  Function to delete all the old asteroids from today
     */
    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteOldAsteroid(START_DATE)
        }
    }

    /**
     * Get LiveData with all the Asteroids of the database
     */
    fun setAllAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }
    }

    /**
     * Get all the asteroids of today
     */
    fun getTodayAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getTodayAsteroid(START_DATE)) {
            it.asDomainModel()
        }
    }

    /**
     * Get the LiveData with a lis of Asteroid according to its dangerousness
     */
    fun getAsteroidsHazardous(isHazardous: Boolean): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidByHazardous(isHazardous)) {
            it.asDomainModel()
        }
    }

}