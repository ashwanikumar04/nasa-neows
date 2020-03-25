package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidItemBinding

class AsteroidAdapter(
    private val asteroids: List<Asteroid>,
    private val itemListener: AsteroidItemListener
) :
    RecyclerView.Adapter<AsteroidAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AsteroidItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroid = asteroids[position]
        holder.bind(asteroid)
        holder.itemView.setOnClickListener {
            itemListener.onAsteroidItemClick(asteroid)
        }
    }

    override fun getItemCount(): Int = asteroids.size

    inner class ViewHolder(private val binding: AsteroidItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Asteroid) {
            binding.asteroid = item
            binding.executePendingBindings()
        }
    }

    interface AsteroidItemListener {
        fun onAsteroidItemClick(asteroid: Asteroid)
    }
}