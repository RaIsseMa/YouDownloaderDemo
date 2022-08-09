package com.dabluecoder.youdownloaderdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.dabluecoder.youdownloaderlib.OnVideoInfoListener
import com.dabluecoder.youdownloaderlib.YouClient
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn)

        btn.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                val client = YouClient()
                client.videoUrl = "https://www.youtube.com/watch?v=VDvr08sCPOc"
//        client.videoUrl = "ggg"
//        client.videoUrl = "https://www.youtube.com/watch?v=SrBLTMs71x0&t=2s"
                client.getVideoInfo (object : OnVideoInfoListener {
                    override fun onSuccess(videoInfo: VideoResponse) {
                        videoInfo.streamingData.adaptiveFormats?.forEach{
                            println("video = ${it.qualityLabel}")
                            println("url = ${it.url}")
                            println("*****************************************************************************************")
                        }
                    }

                    override fun onError(message: String) {
                        println("Error : $message")
                    }

                })
            }
        }

    }
}