package com.dabluecoder.youdownloaderlib

import com.dabluecoder.youdownloaderlib.decoders.DecoderClient
import com.dabluecoder.youdownloaderlib.decoders.ReverseOperation
import com.dabluecoder.youdownloaderlib.decoders.SliceOperation
import com.dabluecoder.youdownloaderlib.decoders.SwapOperation
import com.dabluecoder.youdownloaderlib.extractor.HtmlPageExtractor
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun extractPage() {

        val client = YouClient()
       client.videoUrl = "https://www.youtube.com/watch?v=VDvr08sCPOc"
//        client.videoUrl = "ggg"
//        client.videoUrl = "https://www.youtube.com/watch?v=SrBLTMs71x0&t=2s"
        client.getVideoInfo (object : OnVideoInfoListener{
            override fun onSuccess(videoInfo: VideoResponse) {
                videoInfo.streamingData.adaptiveFormats?.forEach{
                    println("video = ${it.qualityLabel}")
                    println("url = ${it.url}")
                    println("*****************************************************************************************")
                }
            }

            override fun onError(message: String) {
                println("Error : $message")
            }

        })


//        val decoder = DecoderClient()
//        val operations = mutableListOf(
//            ReverseOperation(),
//            SliceOperation(1),
//            SwapOperation(30)
//        )
//        val decodedSignature = decoder.decodeSignature(
//            "Gk_JPuAgG_RGmJnN-MAqUnxLCyxs1ZHw_HsaF3p62mCICYBm7r2X7J1aLv5a3CDUvn-zuAVlJTnF-aGZLOhgwPTWgIARw8JQ0qOFF",
//            operations
//        )

        assertEquals("VDvr08sCPOc","VDvr08sCPOc")
    }
}