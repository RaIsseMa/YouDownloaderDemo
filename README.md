# YouDownloaderLib
![Android](https://img.shields.io/badge/android-green)
![Kotlin](https://img.shields.io/badge/kotlin-grey)
![minSdk](https://img.shields.io/badge/minSdk-21-green)
![targetSdk](https://img.shields.io/badge/targetSdk-31-blue)

`YouDownloaderLib` is a simple android library to retrieve data of a youtube video or short. It extracts all the available qualities and decodes the urls.

* Tech-Stack
  *  [Kotlin](https://developer.android.com/kotlin/first)
  *  [Coroutines](https://developer.android.com/kotlin/coroutines)
  *  [Jsoup](https://jsoup.org/)
 
## Getting Started
To use the library, it is simple and forward. There is a Demo app to use the library.

#### Demo 
The Demo is a simple screen with a button to retrieve data of a youtube video.
````kotlin
  private val url = "https://www.youtube.com/watch?v=F0K6knX606Q"

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
                    println("--------------------------- mime type : ${it.mimeType} ${it.qualityLabel}  url : ${it.url}")
                }
                videoInfo.streamingData.mixedFormats?.forEach {
                    println("--------------------------- mime type : ${it.mimeType}  video url : ${it.url}")
                }
            }
        }

    }
````

The `getVideoData()` method returns a [`VideoResponse`](https://github.com/RaIsseMa/YouDownloaderDemo/blob/master/YouDownloaderLib/src/main/java/com/dabluecoder/youdownloaderlib/pojoclasses/VideoResponse.kt) data class 
that contains the data of the youtube video. The `adaptiveFormats` are separated video and audio formats, for high quality formats you would find them there. You would have to mix the video and audio after you download
them separately.

## Feedback
We highly value your feedback as it helps us continually improve. Please don't hesitate to reach out to us with your comments, suggestions, or any issues you encounter while using the app.

## Contributing
Contributions are always welcome!
