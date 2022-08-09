package com.dabluecoder.youdownloaderlib.extractor

import com.dabluecoder.youdownloaderlib.decoders.DecodeOperation
import com.dabluecoder.youdownloaderlib.decoders.ReverseOperation
import com.dabluecoder.youdownloaderlib.decoders.SliceOperation
import com.dabluecoder.youdownloaderlib.decoders.SwapOperation
import com.dabluecoder.youdownloaderlib.exceptions.DecodeSignatureCipherException
import com.dabluecoder.youdownloaderlib.exceptions.PlayerJsException
import com.dabluecoder.youdownloaderlib.others.Constants
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class JSExtractor(private val playerUrl : String) {

    fun getDecodeOperationsFromPlayerJS(): List<DecodeOperation> {

        if(playerUrl.isEmpty())
            throw PlayerJsException("player url is undefined")

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
        throw Exception(connection.responseMessage)

    }

    private fun extractDecodeFunctions(input : String):List<DecodeOperation>{


        //To decode a signature cipher youtube use 3 functions and 1 main function that calls
        //the 3 functions
        //All functions are random
        //First we extract the main function
        //Second we extract the object name that holds an array of the 3 decode functions from main function
        //Last we extract the order of those functions and it's code to define the decode operation we should
        //use and we extract the index that passes as a parameter.

        val startIndexOfDecodeFunction = input.indexOf("a=a.split(\"\");")
        val subInput = input.substring(startIndexOfDecodeFunction)
        val endIndexOfDecodeFunction = subInput.indexOf("}")
        val decodeFunction = input.substring(startIndexOfDecodeFunction,startIndexOfDecodeFunction+endIndexOfDecodeFunction)
        println("_____________________________________ decode fun = $decodeFunction")

        val objectName = decodeFunction.substring(
            decodeFunction.indexOf(";")+1,
            decodeFunction.indexOf(";")+3
        )
        println("_____________________________________ obj name = $objectName")

        val startIndexOfDecodeFunctionDefinitions = input.indexOf("$objectName={")
        val subInputDefinitions = input.substring(startIndexOfDecodeFunctionDefinitions)
        val endIndexOfDecodeFunctionDefinitions = subInputDefinitions.indexOf("}};")
        val decodeFunctionsDefinition = input.substring(startIndexOfDecodeFunctionDefinitions,startIndexOfDecodeFunctionDefinitions+endIndexOfDecodeFunctionDefinitions+1)
        println("______________________ decod def = $decodeFunctionsDefinition")

//        val funCode = Regex("(\\w+)=function\\(\\w+\\)\\{(\\w+)=\\2\\.split\\(\\x22{2}\\);.*?return\\s+\\2\\.join\\(\\x22{2}\\)}")
//            .find(input)?.value ?: throw DecodeSignatureCipherException("Error to extract decode function")

//        val objectName = Regex("([A-Za-z]*)(?=\\.)").find(funCode.split(";")[1])?.groupValues?.get(0) ?: throw DecodeSignatureCipherException("" +
//                "Failed to extract object's name from decode function")

//        val decodeFunctions = Regex("(?s)var\\s+${objectName}=\\{(\\w+:function\\(\\w+(,\\w+)?\\)\\{(.*?)}),?};")
//            .find(input)?.value ?: throw DecodeSignatureCipherException("Failed to extract decode functions")

        val decodeOperations = mutableListOf<DecodeOperation>()
        val separatedFunctions = decodeFunction.split(";")

        separatedFunctions.forEach{ slice ->
            val functionName = Regex("(?<=\\.)([A-Za-z0-9]*)").find(slice)?.value ?: throw DecodeSignatureCipherException("Failed to extract decode function's name")
            functionName.let {
                if(!it.contains("split") && !it.contains("join")){

                    val startIndexOfDecodeFun = decodeFunctionsDefinition.indexOf(it)

                    val subDecodeFunctions = decodeFunctionsDefinition.substring(startIndexOfDecodeFun)
                    println("_____________________ sub deco / $subDecodeFunctions")
                    val decodeFunctionDefinition = subDecodeFunctions.substring(0,subDecodeFunctions.indexOf("}"))
                    println("________________ decode fun def / $decodeFunctionDefinition")

                    //val decodeFunctionDefinition = Regex("${it}:function\\(\\w+(,\\w+)?\\)\\{(.*?)}").find(decodeFunctionsDefinition)?.value ?: throw DecodeSignatureCipherException("Failed to extract decode function's definition")
                    val index = Regex("(?<=,)\\d+").find(slice)?.value ?: throw DecodeSignatureCipherException("Failed to extract function index")

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