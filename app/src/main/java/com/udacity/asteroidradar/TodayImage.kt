package com.udacity.asteroidradar

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.moshi.Json
import com.squareup.picasso.Picasso

data class TodayImage(
    @field:Json(name = "media_type")
    val mediaType: String,
    val title: String,
    val url: String
) {
    companion object {
        @BindingAdapter("bind:imageUrl")
        @JvmStatic
        fun setText(
            view: ImageView,
            imageUrl: String
        ) {
            if (imageUrl.isNotBlank()) {
                Picasso.with(view.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .into(view);
            }
        }
    }
}