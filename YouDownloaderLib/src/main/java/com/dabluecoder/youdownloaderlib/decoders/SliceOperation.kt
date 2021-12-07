package com.dabluecoder.youdownloaderlib.decoders

class SliceOperation(private val index : Int) : DecodeOperation {

    override fun decode(input: String): String {
        return input.substring(index)
    }
}