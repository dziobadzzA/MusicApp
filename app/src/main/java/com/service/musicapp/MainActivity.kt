package com.service.musicapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.service.musicapp.databinding.MainActivityBinding
import com.service.musicapp.notification.MediaPlayerService
import com.service.musicapp.notification.ServiceCallBack
import com.service.musicapp.ui.main.MusicFragment

class MainActivity: AppCompatActivity(), ServiceCallBack {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MusicFragment())
                .commitNow()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun start() {
        val intent =  Intent(applicationContext, MediaPlayerService::class.java)
        intent.action = MediaPlayerService.PLAY
        startService(intent)
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}