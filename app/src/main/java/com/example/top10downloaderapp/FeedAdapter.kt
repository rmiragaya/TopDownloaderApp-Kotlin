package com.example.top10downloaderapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FeedAdapter(context: Context, private val resource: Int, private  val applications : List<FeedEntry>)
    : ArrayAdapter<FeedEntry>(context, resource) {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null){
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val currentApp = applications[position]

        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtists.text = currentApp.artist
        viewHolder.tvSummary.text = currentApp.summary

        return view
    }

    override fun getCount(): Int {
        return applications.size
    }
}

class ViewHolder (v: View){
    val tvName : TextView = v.findViewById(R.id.tvName)
    val tvArtists : TextView = v.findViewById(R.id.tvArtist)
    val tvSummary : TextView = v.findViewById(R.id.tvSummary)
}

































