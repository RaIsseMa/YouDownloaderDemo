package com.dabluecoder.youdownloaderlib

import android.net.Uri
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

//        val client = YouClient("https://www.youtube.com/watch?v=Q3IkQxzpFfw")
//
//        println(client.getVideoTitle())
//
//
//        val vidResp = client.getVideoAllData()
//
//        vidResp.streamingData.mixedFormats!!.forEach {
//            println(it.url!!)
//        }
//
////        val decoder = DecoderClient()
////        val operations = mutableListOf(
////            ReverseOperation(),
////            SliceOperation(1),
////            SwapOperation(30)
////        )
////        val decodedSignature = decoder.decodeSignature(
////            "Gk_JPuAgG_RGmJnN-MAqUnxLCyxs1ZHw_HsaF3p62mCICYBm7r2X7J1aLv5a3CDUvn-zuAVlJTnF-aGZLOhgwPTWgIARw8JQ0qOFF",
////            operations
////        )
//
//        assertEquals("VDvr08sCPOc","VDvr08sCPOc")
    }

    @Test
    fun `test extract n code functions`(){

        val url = "https://rr1---sn-p5h-uobe.googlevideo.com/videoplayback?expire=1667942286&ei=LnNqY-CxM4aQxN8Pvc-08AI&ip=105.155.29.127&id=o-AEXXlFROvnoMW1aCDi8btcGYbfc1zyeDgHnD_UWnzZvX&itag=299&aitags=133%2C134%2C135%2C136%2C160%2C242%2C243%2C244%2C247%2C278%2C298%2C299%2C302%2C303&source=youtube&requiressl=yes&mh=YN&mm=31%2C29&mn=sn-p5h-uobe%2Csn-p5h-gc5y&ms=au%2Crdu&mv=m&mvi=1&pl=22&initcwndbps=451250&vprv=1&mime=video%2Fmp4&ns=4MA2vi-o1gKJ-cE4ay6IHX4J&gir=yes&clen=106749756&dur=163.446&lmt=1667383261976584&mt=1667920398&fvip=2&keepalive=yes&fexp=24001373%2C24007246&c=WEB&txp=5432434&n=hwfpWE3oWgDWHA&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRAIgTz_-3mDjyQ_KxJ0nxNly_vODmzpZDl_C8C-v0EpJIf0CIEA5lmYMde3po4SDmeZuD35uZOEdov9-bk_vhOCS57RH&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIgfDE7FRPzmj6eYtUtJEN2KvvqTMA9G9cVQiqx1cj3zF0CIQDwQURCti0iLUb_WeKULfMNGHetoLjs7qBqCQCAL4kYjg%3D%3D"
        val uri = Uri.parse(url)
        val n = uri.getQueryParameter("n")
        println("n : $n")
        assertEquals("2","2")

    }
}