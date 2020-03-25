package com.udacity.asteroidradar.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.udacity.asteroidradar.Asteroid
import java.lang.reflect.Type

class MoshiUtil {

    companion object {

        fun serialize(list: List<Asteroid>): String {
            val adapter =
                getAdapter()
            return adapter.toJson(list)
        }

        private fun getAdapter(): JsonAdapter<List<Asteroid>> {
            val moshi = Moshi.Builder().build()
            val type: Type = Types.newParameterizedType(
                MutableList::class.java,
                Asteroid::class.java
            )
            return moshi.adapter(type)
        }

        fun deserialize(data: String): List<Asteroid>? {
            val adapter =
                getAdapter()
            return adapter.fromJson(data)
        }
    }
}