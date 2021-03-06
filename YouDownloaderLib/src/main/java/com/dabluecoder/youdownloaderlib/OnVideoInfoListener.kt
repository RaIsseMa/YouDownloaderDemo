package com.dabluecoder.youdownloaderlib

import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse

interface OnVideoInfoListener {

    fun onSuccess(videoInfo : VideoResponse) : Unit

    fun onError(message : String) : Unit
}