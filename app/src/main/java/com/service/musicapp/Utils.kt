package com.service.musicapp

object Utils {

    fun convertTime(time:Int):String {
        val sec = (time / 1000) % 60
        val minutes = (time / 1000) % 3600 / 60
        val hours = (time / 1000) % 86400 / 3600
        return "$hours:${format(minutes)}:${format(sec)}"
    }

    private fun format(time:Int):String {

        var result = "$time"

        if (time == 0) {
            result = "00"
        } else if (time < 10)
            result = "0$time"

        return result
    }

}