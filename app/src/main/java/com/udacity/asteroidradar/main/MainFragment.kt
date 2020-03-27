package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.data.AsteroidDataDownloader
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.vm.SharedViewModel
import java.util.concurrent.TimeUnit

//Reference https://medium.com/androiddevelopers/workmanager-periodicity-ff35185ff006
class MainFragment : Fragment(),
    AsteroidAdapter.AsteroidItemListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var navController: NavController
    private lateinit var adapter: AsteroidAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment
        )
        binding.asteroidRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.todayImage = viewModel.todayImage.value
        adapter = AsteroidAdapter(emptyList(), this)
        viewModel.asteroidData.observe(requireActivity(), Observer {
            adapter = AsteroidAdapter(it, this)
            binding.asteroidRecycler.adapter = adapter
        })

        viewModel.todayImage.observe(requireActivity(), Observer {
            if ("image" == it.mediaType) {
                binding.todayImage = it
                binding.executePendingBindings()
            }
        })

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val workManager = WorkManager.getInstance(requireActivity().application)

        val workRequest = PeriodicWorkRequest.Builder(
            AsteroidDataDownloader::class.java,
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(1, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(Constants.DOWNLOADER_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.DOWNLOADER_TAG,
            ExistingPeriodicWorkPolicy.KEEP, workRequest
        )

        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(requireActivity(), Observer {
                viewModel.fetchAll()
            })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week -> {
                viewModel.fetchCurrentWeek()
            }
            R.id.show_today -> {
                viewModel.fetchTodayAsteroids()
            }
            R.id.show_all -> {
                viewModel.fetchAll()
            }
        }
        return true
    }

    override fun onAsteroidItemClick(asteroid: Asteroid) {
        val bundle: Bundle = Bundle()
        bundle.putParcelable("selectedAsteroid", asteroid)
        navController.navigate(R.id.action_showDetail, bundle)
    }
}
