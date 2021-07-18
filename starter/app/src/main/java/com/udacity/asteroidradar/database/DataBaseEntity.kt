package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.ui.Asteroid
import com.udacity.asteroidradar.ui.NasaImage

/**
 * Definition of the Asteroid Entity.
 */
@Entity
data class AsteroidEntity(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

/**
 * Extension function to map and get a list of Asteroid that will be used inside the app for
 * showing Asteroid data on the UI
 */
fun List<AsteroidEntity>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

/**
 * Definition of the NasaImage Entity. It holds all the information about the
 * Nasa's Day Image in the database.
 */
@Entity
data class NasaImageEntity(
    val mediaType: String,
    val title: String,
    val url: String,
    @PrimaryKey
    val id: Int
)

/**
 * Return A [NasaImage] object to use inside the app, it provides information about the Nasa's
 * Day Image. That is information is used for setting up the image on the UI and the title of it.
 */
fun asImageDomain(imageEntity: NasaImageEntity?): NasaImage {
    imageEntity?.let {
        return NasaImage(it.mediaType, it.title, it.url)
    }
    return NasaImage("","","")
}