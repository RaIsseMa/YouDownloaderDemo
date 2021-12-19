package com.dabluecoder.youdownloaderlib.extractor

import com.dabluecoder.youdownloaderlib.exceptions.InvalidUrlException
import com.dabluecoder.youdownloaderlib.exceptions.NullDocumentException
import com.dabluecoder.youdownloaderlib.exceptions.NullVideoInfoException
import com.dabluecoder.youdownloaderlib.exceptions.PlayerJsException
import com.dabluecoder.youdownloaderlib.others.Constants
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


//HtmlPageExtractor is the class responsible for extracting the html code from the youtube
//video's page
//From the html code we can extract the video's information like quality,codec,source...
//Some video's urls are encoded so we need to decode them
//To decode a url we need first to extract the javascript code then extract decode functions
//from the javascript code,hence we extract the javascript source from the html code the pass it
//to JsExtractor that will extract those functions

class HtmlPageExtractor(private val videoUrl : String) {

    private var doc: Document? = null

    private fun extractVideoIdFromUrl(): String {

        return try {
            val videoId = videoUrl.substring(
                videoUrl.indexOfFirst { ch -> ch == '=' } + 1,
                videoUrl.indexOfFirst { ch -> ch == '=' } + 12
            )

            if (videoId.length != 11 || videoId.isEmpty()) {
                throw InvalidUrlException("video url is not valid, failed to extract video id from url")
            }
            videoId
        }catch (ex : Exception){
            throw InvalidUrlException("video url is not valid, failed to extract video id from url")
        }
    }

    private fun loadPage() {
        doc = Jsoup
            .connect("${Constants.REQUEST_PAGE_URL}${extractVideoIdFromUrl()}${Constants.REQUEST_PAGE_PARAMETERS}")
            .userAgent(Constants.USER_AGENT)
            .get()
    }

    private fun extractJsonBodyFromPage(page: String): String {
        return page.substring(
            page.indexOfFirst { ch -> ch == '{' },
            page.indexOfLast { ch -> ch == '}' } + 1
        )
    }

    fun extractVideoResponseJson(): VideoResponse {

        if(doc == null)
            loadPage()

        doc?.let { contentPage ->

            val body = contentPage.getElementsByTag("body")

            val scripts = body[0].getElementsByTag("script")
                .filter { element ->
                    element.html().contains("ytInitialPlayerResponse")
                }.map { element ->
                    element.html()
                }[0]



            if(scripts.isEmpty())
                throw NullVideoInfoException("Failed to extract video info from Html page, check if the url is a valid video url")

            val gson = Gson()
            val type = object : TypeToken<VideoResponse>() {}.type

            return try {
                gson.fromJson(extractJsonBodyFromPage(scripts), type)
            }catch (ex : Exception){
                throw ex
            }
        }

        throw NullDocumentException("Html document is null,check if the connection is available and and if the url is a valid video url")
    }

    fun extractPlayerJsUrl() : String{

        if(doc == null)
            loadPage()

        doc?.let { contentPage ->

            val head = contentPage.getElementsByTag("head")

            val player = head[0].getElementsByTag("script")
                .filter { element ->
                    element.attr("src").let {
                        it.contains("player_ias") && it.endsWith(".js")
                    }
                }.map { element ->
                    element.attr("src")
                }[0]

            if(player.isEmpty())
                throw PlayerJsException("Error to extract player url from html document, check if the url is a valid video url")

            println("player url = $player")
            return player

        }
        throw NullDocumentException("Html document is null,check if the connection is available and and if the url is a valid video url")
    }


}