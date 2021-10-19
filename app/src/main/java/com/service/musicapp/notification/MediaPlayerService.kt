package com.service.musicapp.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.service.musicapp.MainActivity
import com.service.musicapp.R
import kotlinx.coroutines.Job

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaSession2Service
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions

class MediaPlayerService: Service() {

    private var mMediaPlayer: MediaPlayer? = null
    private var mManager: MediaSessionManager? = null
    private var mSession: MediaSession? = null
    private var mController: MediaController? = null

    private var notificationManager: NotificationManager? = null // менеджер нотификаций

    // val a = Glide.with(this).load("").diskCacheStrategy(DiskCacheStrategy.ALL)
    lateinit var mediaSessionCompat:MediaSessionCompat
    lateinit var bitmap: Bitmap

    val requestOptions = RequestOptions().override(100)
        .downsample(DownsampleStrategy.CENTER_INSIDE)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", null) // #0
            .addAction(R.drawable.ic_baseline_crop_square_24, "State", null) // #2
            .addAction(R.drawable.ic_baseline_skip_next_24, "Next", null)
            .setContentTitle("Artist")
            .setContentText("My Awesome Band")
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(
                NOTIFICATION_ID)
                .setMediaSession(mediaSessionCompat.sessionToken))
            //.setLargeIcon(bitmap)
            //.setLargeIcon(Glide.with(this)
            //.asBitmap()
            //.load("")
            //.apply(requestOptions)
            //.submit()
            //.get())
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun handleIntent(intent: Intent?) {

        if (intent == null || intent.action == null) return

        val action = intent.action

        when {
            action.equals(PLAY, ignoreCase = true) -> {
                mController!!.transportControls.play()
            }
            action.equals(PAUSE, ignoreCase = true) -> {
                mController!!.transportControls.pause()
            }
            action.equals(PREVIOUS, ignoreCase = true) -> {
                mController!!.transportControls.skipToPrevious()
            }
            action.equals(NEXT, ignoreCase = true) -> {
                mController!!.transportControls.skipToNext()
            }
            action.equals(STOP, ignoreCase = true) -> {
                mController!!.transportControls.stop()
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (mManager == null) {
            initMediaSessions()
        }

        startForegroundAndShowNotification()
        //handleIntent(intent)
        return START_REDELIVER_INTENT
    }

    private fun initMediaSessions() {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.origin)
        mediaSessionCompat = MediaSessionCompat(this, "tag")
        mSession = MediaSession(applicationContext, "mediaSession")
        mController = MediaController(applicationContext, mSession!!.sessionToken)
        mSession!!.setCallback(object : MediaSession.Callback() {

            override fun onPlay() {
                super.onPlay()
                val a = 0
            }

            override fun onPause() {
                super.onPause()
                val b = 0
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
               val c = 0
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                val d = 0
            }

            override fun onStop() {
                super.onStop()
                val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(NOTIFICATION_ID)
                val intent = Intent(applicationContext, MediaPlayerService::class.java)
                stopService(intent)
            }

        })
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onUnbind(intent: Intent): Boolean {
        mSession!!.release()
        return super.onUnbind(intent)
    }

    companion object {
        const val PLAY = "play"
        const val PAUSE = "pause"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val STOP = "stop"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel"
    }

    // была команда на старт
    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, MediaPlayerService::class.java))
        else
            startService(Intent(this, MediaPlayerService::class.java))
    }

    // создаем канал
    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = builder.build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "MusicApp"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, channelName, importance)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }


}