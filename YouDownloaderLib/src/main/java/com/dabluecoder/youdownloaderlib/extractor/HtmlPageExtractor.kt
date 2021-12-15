package com.dabluecoder.youdownloaderlib.extractor

import com.dabluecoder.youdownloaderlib.exceptions.InvalidUrlException
import com.dabluecoder.youdownloaderlib.others.Constants
import com.dabluecoder.youdownloaderlib.pojoclasses.VideoResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HtmlPageExtractor(private val videoUrl : String) {

    private val TAG = "HtmlPageExtractor"

    private var doc: Document? = null

    private fun extractVideoIdFromUrl(): String {
        val videoId = videoUrl.substring(
            videoUrl.indexOfFirst { ch -> ch == '=' } + 1,
            videoUrl.indexOfFirst { ch -> ch == '=' } + 12
        )

        if (videoId.length != 11 || videoId.isEmpty()) {
            throw InvalidUrlException("video url is not valid")
        }
        return videoId
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

    fun extractVideoResponseJson(): VideoResponse? {

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

            val gson = Gson()
            val type = object : TypeToken<VideoResponse>() {}.type

            return gson.fromJson(extractJsonBodyFromPage(scripts), type)
        }

        throw Exception("Document is null")
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

            return player

        }
        throw Exception("Document is null")
    }


}