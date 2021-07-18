package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Interface DAO to interact or access the database
 */
@Dao
interface AsteroidDao {
    //Operations related to the asteroids
    @Insert
    suspend fun insertAll(vararg ast: AsteroidEntity)

    @Query("SELECT * from AsteroidEntity WHERE closeApproachDate >= :start_date AND closeApproachDate <= :end_date")
    suspend fun getAsteroidByDate(start_date: String, end_date: String): AsteroidEntity?

    @Query("SELECT * FROM AsteroidEntity ORDER BY date(closeApproachDate) ASC ")
    fun getAsteroids(): LiveData<List<AsteroidEntity>>

    @Query("DELETE FROM AsteroidEntity WHERE closeApproachDate < :today")
    suspend fun deleteOldAsteroid(today: String)

    @Query("SELECT * FROM AsteroidEntity WHERE closeApproachDate = :today")
    fun getTodayAsteroid(today: String): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM AsteroidEntity WHERE isPotentiallyHazardous = :hazardous ORDER BY closeApproachDate ASC")
    fun getAsteroidByHazardous(hazardous: Boolean): LiveData<List<AsteroidEntity>>

    // Operations related to the Image of the day
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: NasaImageEntity)

    @Query("SELECT * FROM NasaImageEntity")
    fun getPictureOfDay(): LiveData<NasaImageEntity>

    @Query("DELETE FROM NasaImageEntity")
    suspend fun cleanPicture()
}