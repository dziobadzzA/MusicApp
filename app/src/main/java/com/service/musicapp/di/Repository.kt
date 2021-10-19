package com.service.musicapp.di

import com.service.musicapp.model.Music
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class Repository  @Inject constructor() {

    val mutableList: MutableList<Music> = mutableListOf()
    var state:Boolean = false
    var source:String? = null

    fun initItem() {

        if (!source.isNullOrEmpty()) {
            val json = JSONArray(source)

            for (i in 0 until json.length()) {
                val add = json[i] as JSONObject
                mutableList.add(
                    Music(
                        title = add.getString("title"),
                        artist = add.getString("artist"),
                        bitmapUri = add.getString("bitmapUri"),
                        trackUri = add.getString("trackUri"),
                        duration = add.getInt("duration")
                    )
                )
            }

            state = true
        }

    }

}