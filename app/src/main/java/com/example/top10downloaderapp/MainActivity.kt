package com.example.top10downloaderapp

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates.notNull

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val LASTURL = "last_url"
    private val LIMITSEARCH = "limit_search"
    private var cacheUrl = "INVALIDATE"

    private var downloadData: DownloadData? = null
    private var feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            feedUrl= savedInstanceState.getString(LASTURL, feedUrl)
            feedLimit = savedInstanceState.getInt(LIMITSEARCH, feedLimit)
        }

        downloadUrl(feedUrl.format(feedLimit))
    }

    private fun downloadUrl(feedUrl: String){
        if(feedUrl != cacheUrl){
            cacheUrl = feedUrl
            Log.d(TAG, "downloadUrl: start with $feedUrl")
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl)
            Log.d(TAG, "downloadUrl: done")
        } else {
            Log.d(TAG, "no se descargará la misma url")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if(feedLimit == 10){
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else{
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.mnuFree ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaids ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnu10,  R.id.mnu25 -> {
                if(!item.isChecked){
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedlimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedlimit sin cambios")
                }
            }
            R.id.mnuRefresh -> cacheUrl = "INVALIDATE"
            else ->  return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LASTURL, feedUrl)
        Log.d(TAG, "url saved is: $feedUrl")
        outState.putInt(LIMITSEARCH, feedLimit)
        Log.d(TAG, "Limit Search is: $feedLimit")
    }


    companion object {
        private class DownloadData (context : Context, listView : ListView) : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            var propContext : Context by notNull()
            var propListView : ListView by notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplication = ParseApplication()
                parseApplication.parse(result)

                val feedAdapter = FeedAdapter(propContext, R.layout.list_record, parseApplication.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }
}
