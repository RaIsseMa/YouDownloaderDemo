package com.dabluecoder.youdownloaderlib.decoders

class ReverseOperation : DecodeOperation {
    override fun decode(input: String) : String{
        return input.reversed()
    }
}