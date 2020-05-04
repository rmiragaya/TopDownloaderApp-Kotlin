package com.example.top10downloaderapp

class FeedEntry(var name: String = "", var artist: String = "",var releaseDate: String = "",var summary: String = "",var imageUrl: String = "") {
    override fun toString(): String {
        return "FeedEntry(name='$name', artist='$artist', releaseDate='$releaseDate', summary='$summary', imageUrl='$imageUrl')"
    }
}