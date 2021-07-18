package com.udacity.asteroidradar.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data class Asteroid for using inside the app
 */
@Parcelize
data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

/**
 * Data class for the Nasa Day Image, it's used inside the app
 */
data class NasaImage(
    val mediaType: String,
    val title: String,
    val url: String
)