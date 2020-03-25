package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.vm.SharedViewModel

class MainFragment : Fragment() {

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
        binding.asteroidRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.todayImage = viewModel.todayImage.value
        viewModel.asteroidData.observe(requireActivity(), Observer {
            adapter = AsteroidAdapter(it)
            binding.asteroidRecycler.adapter = adapter
        })

        viewModel.todayImage.observe(requireActivity(), Observer {
            if ("image" == it.mediaType) {
                binding.todayImage = it
                binding.executePendingBindings()
            }
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
}
