package com.service.musicapp.ui.main

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.PowerManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.service.musicapp.R
import com.service.musicapp.databinding.FragmentMusicBinding
import com.service.musicapp.di.DaggerAppComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.Context
import android.media.AudioFocusRequest
import com.service.musicapp.notification.ServiceCallBack

import com.vincan.medialoader.MediaLoader


class MusicFragment: Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var musicViewModel: MusicViewModel
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private lateinit var playbackAttributes: AudioAttributes
    private lateinit var audioManager:AudioManager

    lateinit var obmen:ServiceCallBack

    private var audioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    musicViewModel.start()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    musicViewModel.stop()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    musicViewModel.refresh()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModelFactory = DaggerAppComponent.create().viewFactory()
        musicViewModel = ViewModelProvider(this, viewModelFactory)[MusicViewModel::class.java]

        if (!musicViewModel.repository.state) {
            musicViewModel.repository.source =
                activity?.application?.resources?.openRawResource(R.raw.data1)
                    ?.bufferedReader(Charsets.UTF_8).use { it?.readText() }
            musicViewModel.updateItem()
        }

        musicViewModel.item.observe(viewLifecycleOwner, {
            musicViewModel.replaceStateMusicPlayer(false)
            initStateButton(false)
            Glide.with(binding.root.context).load(it.bitmapUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imageView)
            binding.textView.text = musicViewModel.convertTime(it)
            mediaPlayer = MediaPlayer()

            GlobalScope.launch(Dispatchers.IO) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    seekTo(musicViewModel.duration)
                    //val proxyUrl = MediaLoader.getInstance(context).getProxyUrl(it.trackUri)
                    setDataSource(it.trackUri)
                   // setDataSource(proxyUrl)
                    prepare()
                    setWakeMode(activity?.application, PowerManager.PARTIAL_WAKE_LOCK)
                    musicViewModel.initTimer()
                    initStateButton(true)
                    musicViewModel.replaceStateMusicPlayer(true)
                    pruv()
                    binding.seekBar.max = it.duration
                }

                mediaPlayer.setOnCompletionListener {
                    list("next")
                }

            }

        })

        musicViewModel.state.observe(viewLifecycleOwner, {
           procces(it)
        })

        _binding = FragmentMusicBinding.inflate(inflater, container, false)

        binding.seekBar.min = 0

        ///////////////////////////////////
        audioManager = activity?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        playbackAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(playbackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()
        audioManager.requestAudioFocus(focusRequest)
        //////////////////////////////



        obmen.start()

        ///////////////////////////////

        musicViewModel.mediaDuration.observe(viewLifecycleOwner,  {
            if (musicViewModel.getStateMusicPlayer()) {
                binding.seekBar.progress = binding.seekBar.max - it.toInt()
            }
        })


        binding.lastButton.setOnClickListener {
            list("prev")
        }

        binding.nextButton.setOnClickListener {
            list("next")
        }

        binding.startButton.setOnClickListener {
            musicViewModel.start()
        }

        binding.pauseButton.setOnClickListener {
            musicViewModel.stop()
        }

        binding.restartButton.setOnClickListener {
            musicViewModel.refresh()
        }

        return binding.root
    }

    private fun list(way:String) {
        mediaPlayer.stop()
        when (way) {
            "next" -> musicViewModel.next()
            "prev" -> musicViewModel.prev()
        }
    }

    private fun procces(state:Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            if (musicViewModel.getStateMusicPlayer()) {
                if (state)
                    mediaPlayer.start()
                else
                    mediaPlayer.pause()

                binding.stateMusic.text = musicViewModel.textReturn
                musicViewModel.duration = mediaPlayer.currentPosition
            }
        }
    }

    private fun pruv() {
        if (musicViewModel.state.value == true)
            procces(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        musicViewModel.duration = mediaPlayer.currentPosition
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }

    private fun stateButton(state: Boolean, view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            view.isVisible = state
            view.isClickable = state
        }
    }

    private fun initStateButton(state: Boolean) {
        stateButton(state, binding.lastButton)
        stateButton(state, binding.seekBar)
        stateButton(state, binding.nextButton)
        stateButton(state, binding.startButton)
        stateButton(state, binding.pauseButton)
        stateButton(state, binding.restartButton)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ServiceCallBack)
            obmen = context
    }

}