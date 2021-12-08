package com.dabluecoder.youdownloaderlib

import com.dabluecoder.youdownloaderlib.extractor.HtmlPageExtractor
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
        val videoInfo = client.getVideoInfo("https://www.youtube.com/watch?v=VDvr08sCPOc")
        client.decodeVideoUrl(videoInfo.streamingData.mixedFormats[0].signatureCipher)

        assertEquals("VDvr08sCPOc", videoInfo.videoDetails.videoId)
    }
}