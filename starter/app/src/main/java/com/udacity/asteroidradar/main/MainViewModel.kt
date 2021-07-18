package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.AsteroidsDataBase
import com.udacity.asteroidradar.repository.Repository
import com.udacity.asteroidradar.ui.Asteroid
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/**
 * Helper class to Filter the Asteroids under some dates and conditions
 */
enum class AsteroidFilter { ALL, TODAY, DANGEROUS, NOTDANGEROUS }

/**
 * ViewModel for the [MainFragment], it holds all the Asteroid data that will be shown on the main
 * screen.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AsteroidsDataBase.getDataBase(application)
    private val asteroidsRepository = Repository(database)

    // LiveData to navigate to the details screen when the user select an asteroid
    private val _navigateAsteroidDetails = MutableLiveData<Asteroid>()
    val navigateAsteroidDetails: LiveData<Asteroid>
        get() = _navigateAsteroidDetails

    // LiveData that holds all the Asteroid data that will be shown in the RecyclerView
    lateinit var allAsteroids: LiveData<List<Asteroid>>

    // LiveData to show the image of the day and set its title
    val pictureOfDay = asteroidsRepository.pictureOfDay

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        viewModelScope.launch {
            try {
                // Update the image of the day through the repository, which connects
                // with the network and the database. There is data that will be shown on the UI
                asteroidsRepository.refreshImage()
                asteroidsRepository.refreshAsteroids()
            }catch (e: UnknownHostException){
                Log.e("AsteroidRepository", "network error: "+e.message.toString())
                Toast.makeText(application.applicationContext,
                    "There is no internet connection", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Log.e("AsteroidRepository", e.message.toString())
            }

        }
        // Call the function to show all the Asteroid data from today to one week ahead
        asteroidFilter(AsteroidFilter.ALL)
    }

    /**
     * Navigate to the details screen
     */
    fun displayAsteroidDetails(ast: Asteroid) {
        _navigateAsteroidDetails.value = ast
    }

    /**
     *  Change the value of this LiveData after navigating. Avoid Bugs
     */
    fun afterAsteroidDisplayed() {
        _navigateAsteroidDetails.value = null
    }

    /**
     * Filter to change the Asteroid list on the main screen based on the choice pressed by the user
     */
    fun asteroidFilter(asteroidFilter: AsteroidFilter){
        //asteroidsRepository.getTodayAsteroids(asteroidFilter)
        allAsteroids = when (asteroidFilter) {
            AsteroidFilter.ALL -> asteroidsRepository.setAllAsteroids()
            AsteroidFilter.TODAY -> asteroidsRepository.getTodayAsteroids()
            AsteroidFilter.DANGEROUS -> asteroidsRepository.getAsteroidsHazardous(true)
            else -> asteroidsRepository.getAsteroidsHazardous(false)
        }
    }

}

/**
 * Factory to build the MainViewModel with the application parameter
 */
class Factory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}