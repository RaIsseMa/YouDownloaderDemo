package com.dabluecoder.youdownloaderlib

import android.content.Context
import com.dabluecoder.youdownloaderlib.decoders.DecodeOperation
import com.dabluecoder.youdownloaderlib.decoders.DecoderClient
import com.dabluecoder.youdownloaderlib.extractor.HtmlPageExtractor
import com.dabluecoder.youdownloaderlib.extractor.JSExtractor
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse

class YouClient(private val videoUrl : String,private val context: Context) {

    private var videoInfo: VideoResponse? = null
    private var decoderClient : DecoderClient? = null
    private var extractor : HtmlPageExtractor? = null

    fun getVideoTitle(): String{
        if(videoInfo == null){
            loadVideoData()
            return videoInfo!!.videoDetails.title
        }
        return videoInfo!!.videoDetails.title
    }

    fun getVideoThumbnail(): String{
        if(videoInfo == null){
            loadVideoData()
            return videoInfo!!.videoDetails.thumbnail.thumbnails.first().url
        }
        return videoInfo!!.videoDetails.thumbnail.thumbnails.first().url
    }

    suspend fun getVideoAllData(): VideoResponse{
        try{

            if(videoInfo == null)
                loadVideoData()

            val playerJsUrl = extractor!!.extractPlayerJsUrl()

            val jsExtractor = JSExtractor(playerJsUrl)

            decoderClient = DecoderClient(context)

            if (!areVideoSourcesEncoded()) {
                val transformNFunctionCode = jsExtractor.extractNCode()

                videoInfo!!.streamingData.let {
                    it.mixedFormats?.forEach { format ->
                        format.url = decodeOnlyParameterN(format.url?.replace("\u0026", "&")!!,transformNFunctionCode)
                    }
                    it.adaptiveFormats?.forEach { format ->
                        format.url = decodeOnlyParameterN(format.url?.replace("\u0026", "&")!!,transformNFunctionCode)
                    }
                }

                return videoInfo!!
            }

            val decodeOperations = jsExtractor.getDecodeOperationsFromPlayerJS()
            val transformNFunctionCode = jsExtractor.extractNCode()

            videoInfo!!.streamingData.let {
                it.mixedFormats?.forEach { format ->
                    format.url = decodeUrl(
                        format.signatureCipher!!,
                        decodeOperations,
                        transformNFunctionCode
                    )
                }
                it.adaptiveFormats?.forEach { format ->
                    format.url = decodeUrl(
                        format.signatureCipher!!,
                        decodeOperations,
                        transformNFunctionCode
                    )
                }
            }

            return videoInfo!!

        } catch (exp: Exception) {
            throw Exception("YouDownloaderLibException : ${exp.message!!}")
        }
    }

    private fun loadVideoData() {
        try {
            extractor = HtmlPageExtractor(videoUrl)
            videoInfo = extractor!!.extractVideoResponseJson()

        } catch (ex: Exception) {
            throw Exception("YouDownloaderLibException : ${ex.message}")
        }
    }

    private fun areVideoSourcesEncoded() =
        videoInfo!!.streamingData.mixedFormats?.first()?.url == null || videoInfo!!.streamingData.adaptiveFormats?.first()
            ?.url == null

    private suspend fun decodeUrl(signatureCipher : String,decodeOperations : List<DecodeOperation>,nFunctionCode : String?) : String{
        val decryptedUrl =  decoderClient!!.decodeSignature(
            signatureCipher,
            decodeOperations
        )
        return decoderClient!!.decodeParameterN(decryptedUrl,nFunctionCode)
    }

    private suspend fun decodeOnlyParameterN(url : String,nFunctionCode: String?): String{
        return decoderClient!!.decodeParameterN(url,nFunctionCode)
    }
}