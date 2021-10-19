package com.service.musicapp.ui.main

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.service.musicapp.Utils
import com.service.musicapp.di.Repository
import com.service.musicapp.model.Music
import com.service.musicapp.model.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicViewModel @Inject constructor(val repository: Repository): ViewModel() {

    private var _item = MutableLiveData<Music>()
    val item: LiveData<Music> get() = _item

    private var index:Int = 0

    private var _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean> get() = _state

    private var initializeMusicPlayer = false
    var textReturn = ""
    var duration = 0

    private var timer:Timer

    private var _mediaDuration = MutableLiveData<Int>()
    val mediaDuration: LiveData<Int> get() = _mediaDuration

    private var timerListener: CountDownTimer

    init {
        repository.initItem()
        _item = MutableLiveData<Music>()
        timer = Timer(0)
        updateItem()
        timerListener = getCountDownTimer(timer)
        _mediaDuration.value = 0
    }

    fun replaceStateMusicPlayer(state:Boolean) {
        initializeMusicPlayer = state
    }

    fun getStateMusicPlayer():Boolean = initializeMusicPlayer

    fun stop() {
        if (_state.value == true || _state.value == null)
            if (returnRightTextStateAndMoveState("Пауза")) {
                _state.value = false
                timerListener.cancel()
            }
    }

    fun start() {
        if (_state.value == false || _state.value == null)
            if (returnRightTextStateAndMoveState("Играет")) {
                _state.value = true
                timerListener = getCountDownTimer(timer)
                timerListener.start()
            }
    }

    fun refresh() {
        if (_state.value == true || _state.value == null)
            if (returnRightTextStateAndMoveState("Остановка")) {
                duration = 0
                _state.value = false
                timerListener.cancel()
                initTimer()
            }
    }

    private fun returnRightTextStateAndMoveState(text:String):Boolean {

        var result = false
        if (initializeMusicPlayer) {
            textReturn = text
            result = true
        }
        else
            textReturn = ""
        return result

    }

    fun next() {
        if (repository.mutableList.size - 1 <= index)
            index = 0
        else
            index++
        updateItem()
    }

    fun prev() {
        if (index == 0)
            index = repository.mutableList.size - 1
        else
            index--
        updateItem()
    }

    fun updateItem() {
        if (!repository.state)
            repository.initItem()
        if (repository.mutableList.size > index)
            _item.value = repository.mutableList[index]
        duration = 0
    }

    fun convertTime(it: Music):String = it.artist + " трек " + it.title + " длительность: " + Utils.convertTime(it.duration)

    fun initTimer() {
        GlobalScope.launch(Dispatchers.Main) {
            timer.initTimer = item.value?.duration!!
            _mediaDuration.value = timer.initTimer
        }
    }

    private fun getCountDownTimer(time:Timer): CountDownTimer {

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {

            override fun onTick(millisUntilFinished: Long) {
                time.initTimer -= UNIT_TEN_MS.toInt()
                _mediaDuration.value = time.initTimer
            }
            override fun onFinish() {

            }
        }

    }

    companion object {
        const val PERIOD = 1000L * 60L * 60L * 24L
        const val UNIT_TEN_MS = 1000L
    }

}