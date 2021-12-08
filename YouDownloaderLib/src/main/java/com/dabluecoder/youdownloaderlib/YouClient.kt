package com.dabluecoder.youdownloaderlib

import android.text.Html
import com.dabluecoder.youdownloaderlib.decoders.DecoderClient
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

    fun decodeVideoUrl(signatureCipher : String){
        getPlayerJs()
        val jsExtractor = JSExtractor()
        jsExtractor.playerUrl = playerJsUrl!!
        val decodeOperations = jsExtractor.getDecodeOperationsFromPlayerJS()
        val decoderClient = DecoderClient()
        print("url = "+decoderClient.decodeSignature(signatureCipher,decodeOperations))
    }

    private fun getPlayerJs(){
        playerJsUrl = extractor!!.extractPlayerJsUrl()
    }

    private fun initializeExtractorIfNull(){
        if (extractor == null)
            extractor = HtmlPageExtractor()
    }


}