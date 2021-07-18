package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.database.NasaImageEntity
import com.udacity.asteroidradar.ui.Asteroid

/**
 * Class to match the Asteroids List from the network
 */
data class NetworkData(val asteroids: ArrayList<Asteroid>)

/**
 * Convert Network results to database objects
 */
fun NetworkData.asDataBaseModel(): Array<AsteroidEntity> {
    return asteroids.map {
        AsteroidEntity(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

/**
 * Class for mapping the Nasa's day image Network Result using Moshi
 */
data class PictureOfDay(
    @Json(name = "media_type") val mediaType: String,
    val thumbnail_url: String?, // thumbnail image in case the result is a video
    val title: String,
    val url: String
) {
    /**
     * Convert the network result to a database object
     */
    fun toImageDatabase(): NasaImageEntity {
        // if it was a video there will be a thumbnail image of the video, it sometimes can be equals ""
        return if(thumbnail_url != null){
            NasaImageEntity(mediaType, title, thumbnail_url,1)// the id will be overwritten by SqlLite id constraints
        }else
            NasaImageEntity(mediaType, title, url,1)
    }
}

