package com.dabluecoder.youdownloaderlib.decoders

class ReserveOperation : DecodeOperation {
    override fun decode(input: String) : String{
        return input.reversed()
    }
}