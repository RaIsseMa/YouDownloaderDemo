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

    private val TAG = "MainActivity"
    private val url = "https://www.youtube.com/watch?v=Q3IkQxzpFfw"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn)

        btn.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                val youClient = YouClient(url)
                println("$TAG initialize client $url")
                val videoTitle = youClient.getVideoTitle()
                println("$TAG get video title $videoTitle")
                val videoThumbnail = youClient.getVideoThumbnail()
                println("$TAG get video thumbnail $videoThumbnail")
            }
        }

    }
}