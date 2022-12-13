package com.dabluecoder.youdownloaderdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.dabluecoder.youdownloaderlib.YouClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val url = "https://www.youtube.com/shorts/tvECr4fiu2o"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn)

        btn.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val youClient = YouClient(url, this@MainActivity)
                val videoInfo = youClient.getVideoData()
                videoInfo.streamingData.adaptiveFormats?.forEach {
                    println("--------------------------- url : ${it.url}")
                }
            }
        }

    }
}