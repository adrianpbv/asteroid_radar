package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.bindRecyclerView
import com.udacity.asteroidradar.databinding.FragmentMainBinding

/**
 * This fragment sets up all the main content of the app, the Asteroid list and the Nasa's Image Day
 */
class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    private lateinit var adapter: AsteroidAdapter
    /**
     * The creation of the ViewModel is delayed, it can be referenced once the views have been
     * created in the fragment's view hierarchy.
     */
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(requireActivity())
        ViewModelProvider(this, Factory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        adapter = AsteroidAdapter(AsteroidClickListener { asteroid ->
            viewModel.displayAsteroidDetails(asteroid)
        })

        // Setting up the RecyclerView's Adapter and its callback with each asteroid
        binding.asteroidRecycler.adapter = adapter

        // Observer for navigating to the selected Asteroid. After reset the ViewModel
        // for another navigation event
        viewModel.navigateAsteroidDetails.observe(viewLifecycleOwner, Observer {
            it?.let {
                // Find the NavController from the Fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.afterAsteroidDisplayed()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.asteroidFilter(
            when (item.itemId) {
                R.id.show_all_menu -> AsteroidFilter.ALL
                R.id.show_today_menu -> AsteroidFilter.TODAY
                R.id.show_hazardous_menu -> AsteroidFilter.DANGEROUS
                else -> AsteroidFilter.NOTDANGEROUS
            }
        )
        observeAsteroid()
        binding.executePendingBindings()
        return true
    }

    /**
     * Update the LiveData of the [allAsteroids] observer, as it changes when the user selects any of
     * the options in the menu.
     */
    private fun observeAsteroid(){
        viewModel.allAsteroids.observe(viewLifecycleOwner, Observer {
            bindRecyclerView(binding.asteroidRecycler, it)
        })
    }
}
