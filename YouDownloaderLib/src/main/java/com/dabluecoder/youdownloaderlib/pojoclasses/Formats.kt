package com.dabluecoder.youdownloaderlib.pojoclasses


data class Formats(
    val mimeType: String,
    val width: Int,
    val height: Int,
    val bitrate: Long,
    val lastModified: String,
    val contentLength: String,
    val quality: String,
    val fps: Int,
    val qualityLabel: String,
    val audioQuality: String,
    val signatureCipher: String,
    var url : String?
)
