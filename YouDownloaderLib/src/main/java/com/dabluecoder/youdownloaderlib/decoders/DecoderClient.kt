package com.dabluecoder.youdownloaderlib.decoders

import java.net.URLDecoder


class DecoderClient {

    fun decodeSignature(signature : String,decodeOperations : List<DecodeOperation>):String{
        val signatureQuery = signature.split('\u0026')
        var signatureEncoded = signatureQuery[0].substring(signatureQuery[0].indexOfFirst { ch -> ch == '=' }+1)

        signatureEncoded = URLDecoder.decode(signatureEncoded,"utf-8")

        decodeOperations.forEach { decodeOperation ->
            signatureEncoded = decodeOperation.decode(signatureEncoded)
        }

        return "${URLDecoder.decode(signatureQuery[2].substring(4),"utf-8")}&${signatureQuery[1].substring(3)}=$signatureEncoded"
    }

}