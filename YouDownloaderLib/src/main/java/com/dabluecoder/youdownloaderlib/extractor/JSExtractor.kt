package com.dabluecoder.youdownloaderlib.extractor

import com.dabluecoder.youdownloaderlib.others.Constants
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class JSExtractor {

    var playerUrl : String = ""

    fun getPlayerJSRaw(){

        if(playerUrl.isEmpty())
            throw Exception("Invalid player url")

        val url = URL("${Constants.REQUEST_PLAYER_BASE_URL}$playerUrl")
        val connection = url.openConnection() as HttpsURLConnection

        connection.let {
            it.requestMethod = "GET"
            it.setRequestProperty("User-Agent",Constants.USER_AGENT)
            it.doInput = true
            it.doOutput = false
        }

        val respCode = connection.responseCode
        if(respCode == HttpsURLConnection.HTTP_OK){
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            extractDecodeFunctions(response)
        }
    }

    private fun extractDecodeFunctions(input : String){
        val funCode = Regex("(\\w+)=function\\(\\w+\\)\\{(\\w+)=\\2\\.split\\(\\x22{2}\\);.*?return\\s+\\2\\.join\\(\\x22{2}\\)\\}")
            .find(input)?.value ?: throw Exception("Error to extract decode function")

        println("main fun = $funCode")

        val decodeFunctionsNames = mutableListOf<String>()
        val separatedFunctions = funCode.split(";")

        separatedFunctions.forEach{ slice ->
            val functionName = Regex("(?<=\\.)([A-Za-z]*)").find(slice)?.value ?: return
            functionName.let {
                if(!it.contains("split") && !it.contains("join") && !decodeFunctionsNames.contains(it.substring(0)))
                    decodeFunctionsNames.add(it.substring(0))
            }
        }

        val objectName = Regex("([A-Za-z]*)(?=\\.)").find(funCode.split(";")[1])?.groupValues?.get(0)
        println("object name = $objectName")

        val decodeFunctions = Regex("(?s)var\\s+${objectName}=\\{(\\w+:function\\(\\w+(,\\w+)?\\)\\{(.*?)\\}),?\\};")
            .find(input)?.value

        print("decode functions = $decodeFunctions")
    }
}