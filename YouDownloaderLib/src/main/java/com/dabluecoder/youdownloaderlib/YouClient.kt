package com.dabluecoder.youdownloaderlib

import android.text.Html
import com.dabluecoder.youdownloaderlib.extractor.HtmlPageExtractor
import com.dabluecoder.youdownloaderlib.extractor.JSExtractor
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse


class YouClient {

    private var videoInfo: VideoResponse? = null
    private var playerJsUrl: String? = null

    private var extractor: HtmlPageExtractor? = null

    fun getVideoInfo(videoUrl: String): VideoResponse {

        initializeExtractorIfNull()

        extractor!!.videoUrl = videoUrl
        videoInfo = extractor!!.extractVideoResponseJson()
        return videoInfo!!

    }

    public fun getFunctions(){
        getPlayerJs()
        val jsExtractor = JSExtractor()
        jsExtractor.playerUrl = playerJsUrl!!
        jsExtractor.getPlayerJSRaw()
    }

    private fun getPlayerJs(){
        playerJsUrl = extractor!!.extractPlayerJsUrl()
    }

    private fun initializeExtractorIfNull(){
        if (extractor == null)
            extractor = HtmlPageExtractor()
    }


}