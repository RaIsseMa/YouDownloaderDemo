package com.dabluecoder.youdownloaderlib.pojoclasses

data class VideoResponse(
    val playabilityStatus : PlayabilityStatus,
    val streamingData : StreamingData,
    val videoDetails : VideoDetails
)
