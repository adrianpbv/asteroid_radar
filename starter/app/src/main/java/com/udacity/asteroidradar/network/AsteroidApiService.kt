package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

/**
 * Interface to communicate with the nasa's server
 */
interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("address") start: String,
        @Query("end_date") end: String
    ): String

    @GET("planetary/apod")
    suspend fun getNasaDayImage(
        //@Query("api_key") key: String,
        @Query("thumbs") thumbnail: Boolean
    ): PictureOfDay
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object AsteroidsApi {
    val retrofitService : AsteroidApiService by lazy { RetrofitClient.getClient().create(AsteroidApiService::class.java) }
}

object RetrofitClient {
    private lateinit var retrofit: Retrofit
    fun getClient(): Retrofit {
        if (!::retrofit.isInitialized) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { apiKeyInterceptor(it) }
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }
        return retrofit
    }

    private fun apiKeyInterceptor(it: Interceptor.Chain): Response {
        val originalRequest = it.request()
        val originalHttpUrl = originalRequest.url()

        val newHttpUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.NASA_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newHttpUrl)
            .build()

        return it.proceed(newRequest)
    }
}