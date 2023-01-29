package com.dabluecoder.youdownloaderlib.decoders

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import com.dabluecoder.youdownloaderlib.others.Constants.MAIN_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLDecoder


class DecoderClient(private val context: Context) {

    private val decodedN = mutableMapOf<String,String>()

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
        val uri = Uri.parse(url)
        val n = uri.getQueryParameter("n")
        if( n== null || n.isEmpty() || functionCode == null){
            Log.w(MAIN_TAG,"parameter n is null or function code is null")
            return url
        }
        var newN = "unKnown"
        if(decodedN.contains(n)){
            return url.replace(n,decodedN[n]!!)
        }
        withContext(Dispatchers.Main){
            val wbView = WebView(context)
            wbView.settings.javaScriptEnabled = true

            wbView.evaluateJavascript("($functionCode) ('$n');") { transformedN ->
                newN = transformedN?.replace("\"","") ?: ""
            }
        }

        while(true){
            delay(150)
            if(newN != "unKnown"){
                break
            }
        }
        if(newN == "") {
            Log.w(MAIN_TAG,"could not decode parameter n")
            return url
        }
        decodedN[n] = newN
        Log.i(MAIN_TAG,"parameter n decoded successfully")
        return url.replace(n,newN)
    }

}