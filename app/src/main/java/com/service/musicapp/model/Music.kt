package com.service.musicapp.model

data class Music(var title:String = "",
                 var artist:String = "",
                 var bitmapUri:String = "",
                 var trackUri:String = "",
                 var duration:Int = 0
)