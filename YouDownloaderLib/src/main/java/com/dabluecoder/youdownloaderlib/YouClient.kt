package com.dabluecoder.youdownloaderlib

import android.text.Html
import com.dabluecoder.youdownloaderlib.decoders.DecoderClient
import com.dabluecoder.youdownloaderlib.extractor.HtmlPageExtractor
import com.dabluecoder.youdownloaderlib.extractor.JSExtractor
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse


class YouClient {

    private var videoInfo: VideoResponse? = null
    private var listener : OnVideoInfoListener? = null

    var videoUrl = ""

    fun getVideoInfo(): VideoResponse {

        this.listener = listener

        if(videoUrl.isEmpty())
            this.listener?.onError("Video' url is undefined")

        val extractor = HtmlPageExtractor(videoUrl)

        videoInfo = extractor.extractVideoResponseJson()

        val playerJsUrl = extractor.extractPlayerJsUrl()
        val jsExtractor = JSExtractor(playerJsUrl)
        val decodeOperations = jsExtractor.getDecodeOperationsFromPlayerJS()
        val decoderClient = DecoderClient()
        videoInfo!!.streamingData.adaptiveFormats.forEach { format ->
            format.url = decoderClient.decodeSignature(
                format.signatureCipher,
                decodeOperations
            )
        }
        return videoInfo!!

    }



}