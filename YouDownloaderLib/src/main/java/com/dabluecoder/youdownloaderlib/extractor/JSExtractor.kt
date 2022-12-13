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

    private var jsPlayerCode : String? = null

    private val decodeFunctionsCache = mutableMapOf<String,List<DecodeOperation>>()
    private val nParameterDecodeFunctions = mutableMapOf<String,String>()

    fun getDecodeOperationsFromPlayerJS(): List<DecodeOperation> {

        if(decodeFunctionsCache.contains(playerUrl))
            return decodeFunctionsCache[playerUrl]!!

        if(jsPlayerCode == null)
            getPlayerCode()

        return extractDecodeFunctions()
    }


    private fun extractDecodeFunctions():List<DecodeOperation>{

        //To decode a signature cipher youtube use 3 functions and 1 main function that calls
        //the 3 functions
        //All functions are random
        //First we extract the main function
        //Second we extract the object name that holds an array of the 3 decode functions from main function
        //Last we extract the order of those functions and it's code to define the decode operation we should
        //use and we extract the index that passes as a parameter.

        val startIndexOfDecodeFunction = jsPlayerCode!!.indexOf("a=a.split(\"\");")
        val subInput = jsPlayerCode!!.substring(startIndexOfDecodeFunction)
        val endIndexOfDecodeFunction = subInput.indexOf("}")
        val decodeFunction = jsPlayerCode!!.substring(startIndexOfDecodeFunction,startIndexOfDecodeFunction+endIndexOfDecodeFunction)

        val objectName = decodeFunction.substring(
            decodeFunction.indexOf(";")+1,
            decodeFunction.indexOf(";")+3
        )

        val startIndexOfDecodeFunctionDefinitions = jsPlayerCode!!.indexOf("$objectName={")
        val subInputDefinitions = jsPlayerCode!!.substring(startIndexOfDecodeFunctionDefinitions)
        val endIndexOfDecodeFunctionDefinitions = subInputDefinitions.indexOf("}};")
        val decodeFunctionsDefinition = jsPlayerCode!!.substring(startIndexOfDecodeFunctionDefinitions,startIndexOfDecodeFunctionDefinitions+endIndexOfDecodeFunctionDefinitions+1)

        val decodeOperations = mutableListOf<DecodeOperation>()
        val separatedFunctions = decodeFunction.split(";")

        separatedFunctions.forEach{ slice ->
            val functionName = Regex("(?<=\\.)([A-Za-z0-9]*)").find(slice)?.value ?: throw DecodeSignatureCipherException("Failed to extract decode function's name")
            functionName.let {
                if(!it.contains("split") && !it.contains("join")){

                    val startIndexOfDecodeFun = decodeFunctionsDefinition.indexOf(it)

                    val subDecodeFunctions = decodeFunctionsDefinition.substring(startIndexOfDecodeFun)
                    val decodeFunctionDefinition = subDecodeFunctions.substring(0,subDecodeFunctions.indexOf("}"))

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

        decodeFunctionsCache[playerUrl] = decodeOperations.toList()
        return decodeOperations.toList()
    }

    fun extractNCode(): String? {
        //extract functions of transforming parameter N

        if(nParameterDecodeFunctions.contains(playerUrl))
            return nParameterDecodeFunctions[playerUrl]

        if(jsPlayerCode == null)
            getPlayerCode()

        return try {
            val startOfFunction = Regex("(?<=)[a-z]{3}=function\\(a\\)\\{var b=a\\.split").find(jsPlayerCode!!)?.value  ?: throw Exception("Error to extract name of n function")

            val startIndexOfFunction = jsPlayerCode!!.indexOf(startOfFunction)
            val subInp = jsPlayerCode!!.substring(startIndexOfFunction + "pla=".length)
            val endIndexOfFunction = subInp.indexOf("return b.join(\"\")};")
            val decodeFunction = subInp.substring(0, endIndexOfFunction + "return b.join(\"\")};".length-1)
            nParameterDecodeFunctions[playerUrl] = decodeFunction
            decodeFunction
        }catch (ex : Exception){
            println("Error occurred while extracting n functions : ${ex.printStackTrace()}")
            null
        }

    }

    private fun getPlayerCode(){

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
            jsPlayerCode = connection.inputStream.bufferedReader().use { it.readText() }
            return
        }
        throw Exception(connection.responseMessage)
    }

}