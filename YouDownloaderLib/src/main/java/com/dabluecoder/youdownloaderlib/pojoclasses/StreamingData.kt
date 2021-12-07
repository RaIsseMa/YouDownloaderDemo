package com.dabluecoder.youdownloaderlib.pojoclasses

import com.google.gson.annotations.SerializedName

data class StreamingData(
    @SerializedName("formats") val mixedFormats : List<Formats>,
    @SerializedName("adaptiveFormats") val adaptiveFormats : List<Formats>
)
