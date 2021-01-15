package com.dan.codessert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

class About : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val wvBrowser: WebView = view.findViewById(R.id.webAbout)
        wvBrowser.loadUrl("file:///android_asset/about.html")
        wvBrowser.settings.javaScriptEnabled = true
        return view

    }

}