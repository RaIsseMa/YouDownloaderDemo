package com.dabluecoder.youdownloaderlib.decoders

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLDecoder


class DecoderClient(private val context: Context) {


    fun decodeSignature(signature : String,decodeOperations : List<DecodeOperation>):String{
        val signatureQuery = signature.split('\u0026')
        var signatureEncoded = signatureQuery[0].substring(signatureQuery[0].indexOfFirst { ch -> ch == '=' }+1)

        signatureEncoded = URLDecoder.decode(signatureEncoded,"utf-8")

        decodeOperations.forEach { decodeOperation ->
            signatureEncoded = decodeOperation.decode(signatureEncoded)
        }

        return "${URLDecoder.decode(signatureQuery[2].substring(4),"utf-8")}&${signatureQuery[1].substring(3)}=$signatureEncoded"
    }

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun decodeParameterN(url : String,functionCode : String?) : String{
        val testUrl = "https://rr1---sn-p5h-uobe.googlevideo.com/videoplayback?expire=1667962045&ei=XcBqY4LQBJ6zxN8PnqyTqAk&ip=105.155.29.127&id=o-ABQrL1Fa34j-HtsIpkTtC-qerWCFzhF0ekrVnG5zYUOs&itag=18&source=youtube&requiressl=yes&mh=YN&mm=31%2C29&mn=sn-p5h-uobe%2Csn-p5h-gc5y&ms=au%2Crdu&mv=m&mvi=1&pl=22&initcwndbps=345000&vprv=1&mime=video%2Fmp4&ns=bq9_l5fQEbqNxX37_LBjHMMJ&gir=yes&clen=14200357&ratebypass=yes&dur=163.514&lmt=1667381569840909&mt=1667939587&fvip=2&fexp=24001373%2C24007246&c=WEB&txp=5438434&n=1CX9XbXw_rFBF_f4Qlq&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRgIhAJs-bloY1w2WhlpXlnZ19lviOx8XI63970iTYuZ42Y2wAiEAqfgFFaUdnXtRD14cWb143lcCauPjQm9dIvkkrJLOrmE%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRgIhAOdzWA10AijbVIHvV8jtyry8ZvcV57e8BhXchAQSUfoPAiEAyKOExJnCUOsZ39gJHtk1ZM2Zv_9Q70qlI9mhQMJ52FE%3D"
        val uri = Uri.parse(testUrl)
        val n = uri.getQueryParameter("n")
        if( n== null || n.isEmpty() || functionCode == null)
            return url
        var newN = "unKnown"
        withContext(Dispatchers.Main){
            val wbView = WebView(context)
            wbView.settings.javaScriptEnabled = true

            wbView.evaluateJavascript("($functionCode) ($n);") { transformedN ->
                newN = transformedN?: ""
            }
        }

        while(newN == "unknown"){

        }
        if(newN == "")
            return url
        println("------------------------------------------ $n/$newN")
        return url.replace(n,newN)
    }

}