package com.dabluecoder.youdownloaderlib.pojoclasses

data class VideoDetails(
    val videoId : String,
    val title : String,
    val lengthSeconds : String,
    val keywords : List<String>,
    val shortDescription : String,
    val viewCount : String,
    val author : String,
    val thumbnail : Thumbnail
)
