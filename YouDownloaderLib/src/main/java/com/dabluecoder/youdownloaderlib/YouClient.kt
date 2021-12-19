package com.dabluecoder.youdownloaderlib

import android.text.Html
import com.dabluecoder.youdownloaderlib.decoders.DecodeOperation
import com.dabluecoder.youdownloaderlib.decoders.DecoderClient
import com.dabluecoder.youdownloaderlib.extractor.HtmlPageExtractor
import com.dabluecoder.youdownloaderlib.extractor.JSExtractor
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse


class YouClient {

    private var videoInfo: VideoResponse? = null
    private var listener: OnVideoInfoListener? = null
    private var decoderClient : DecoderClient? = null
    var videoUrl = ""

    fun getVideoInfo(listener: OnVideoInfoListener) {

        this.listener = listener

        if (videoUrl.isEmpty()) {
            this.listener?.onError("Video url is undefined")
            return
        }

        try {
            val extractor = HtmlPageExtractor(videoUrl)

            videoInfo = extractor.extractVideoResponseJson()

            if (!areVideoSourcesEncoded()) {

                videoInfo!!.streamingData.let {
                    it.mixedFormats?.forEach { format ->
                        format.url = format.url?.replace("\u0026", "&")
                    }
                    it.adaptiveFormats?.forEach { format ->
                        format.url = format.url?.replace("\u0026", "&")
                    }
                }
                this.listener?.onSuccess(videoInfo!!)
                return
            }

            val playerJsUrl = extractor.extractPlayerJsUrl()

            val jsExtractor = JSExtractor(playerJsUrl)

            val decodeOperations = jsExtractor.getDecodeOperationsFromPlayerJS()

            decoderClient = DecoderClient()
            videoInfo!!.streamingData.let {
                it.mixedFormats?.forEach { format ->
                    format.url = decodeUrl(
                        format.signatureCipher!!,
                        decodeOperations
                    )
                }
                it.adaptiveFormats?.forEach { format ->
                    format.url = decodeUrl(
                        format.signatureCipher!!,
                        decodeOperations
                    )
                }
            }

            this.listener?.onSuccess(videoInfo!!)

        } catch (exp: Exception) {
            this.listener?.onError("YouDownloader error = ${exp.message!!}")
        }

    }

    private fun areVideoSourcesEncoded() =
        videoInfo!!.streamingData.mixedFormats?.first()?.url == null || videoInfo!!.streamingData.adaptiveFormats?.first()
            ?.url == null

    private fun decodeUrl(signatureCipher : String,decodeOperations : List<DecodeOperation>) : String{
        return decoderClient!!.decodeSignature(
            signatureCipher,
            decodeOperations
        )
    }
}