package com.dabluecoder.youdownloaderlib.extractor

import com.dabluecoder.youdownloaderlib.decoders.DecodeOperation
import com.dabluecoder.youdownloaderlib.decoders.ReverseOperation
import com.dabluecoder.youdownloaderlib.decoders.SliceOperation
import com.dabluecoder.youdownloaderlib.decoders.SwapOperation
import com.dabluecoder.youdownloaderlib.others.Constants
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class JSExtractor {

    var playerUrl : String = ""

    fun getDecodeOperationsFromPlayerJS(): List<DecodeOperation> {

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
            return extractDecodeFunctions(response)
        }
        return emptyList()
    }

    private fun extractDecodeFunctions(input : String):List<DecodeOperation>{
        val funCode = Regex("(\\w+)=function\\(\\w+\\)\\{(\\w+)=\\2\\.split\\(\\x22{2}\\);.*?return\\s+\\2\\.join\\(\\x22{2}\\)}")
            .find(input)?.value ?: throw Exception("Error to extract decode function")

        println("main fun = $funCode")


        val objectName = Regex("([A-Za-z]*)(?=\\.)").find(funCode.split(";")[1])?.groupValues?.get(0)
        println("object name = $objectName")

        val decodeFunctions = Regex("(?s)var\\s+${objectName}=\\{(\\w+:function\\(\\w+(,\\w+)?\\)\\{(.*?)}),?};")
            .find(input)?.value

        println("decode functions : $decodeFunctions")

        val decodeOperations = mutableListOf<DecodeOperation>()
        val separatedFunctions = funCode.split(";")

        separatedFunctions.forEach{ slice ->
            val functionName = Regex("(?<=\\.)([A-Za-z]*)").find(slice)?.value ?: return emptyList()
            functionName.let {
                if(!it.contains("split") && !it.contains("join")){
                    val decodeFunctionDefinition = Regex("${it}:function\\(\\w+(,\\w+)?\\)\\{(.*?)}").find(decodeFunctions!!)?.value
                    val index = Regex("\\d+").find(slice)?.value

                    println("decode function definition : $decodeFunctionDefinition")
                    println("index = $index")

                    if(decodeFunctionDefinition==null || index == null)
                        return decodeOperations.toList()

                    when{
                        decodeFunctionDefinition.contains("splice")->{
                            decodeOperations.add(SliceOperation(index.toInt()))
                        }
                        decodeFunctionDefinition.contains("reverse")->{
                            decodeOperations.add(ReverseOperation())
                        }
                        else -> {
                            decodeOperations.add(SwapOperation(index.toInt()))
                        }
                    }

                }
            }
        }

        return decodeOperations.toList()
    }
}