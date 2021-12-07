package com.dabluecoder.youdownloaderlib.decoders

class SwapOperation(private val index : Int) : DecodeOperation {

    override fun decode(input: String): String {
        val chars = input.toMutableList()
        val c = chars[0]
        chars[0] = chars[index%chars.count()]
        chars[index % chars.count()] = c
        return chars.joinToString("")
    }

}