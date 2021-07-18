package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Defining the Room Database and a function to get a reference of it.
 */
@Database(entities = [AsteroidEntity::class, NasaImageEntity::class], version = 1, exportSchema = false)
abstract class AsteroidsDataBase: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao // to access the db

    //companion object provides a function to get a reference to the database
    companion object{
        @Volatile //ensure INSTANCE will always be up-to-date to all threads
        private lateinit var INSTANCE: AsteroidsDataBase

        fun getDataBase(context: Context): AsteroidsDataBase{
            // only one thread of execution at a time can access this code
            synchronized(this){
                // the db is initialized once
                if(!::INSTANCE.isInitialized){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AsteroidsDataBase::class.java,
                    "asteroids").build()
                }
            }
            return INSTANCE
        }
    }
}