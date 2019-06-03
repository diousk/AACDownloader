package com.example.downloader.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.downloader.Data
import com.example.downloader.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        download.setOnClickListener {
//            val sampleUrl = Data.sampleUrls[(0..Data.sampleUrls.size).random()]
            val sampleUrl = Data.sampleUrls[1]
            viewModel.download(sampleUrl)
        }

        delete.setOnClickListener {
            viewModel.req()
        }
        viewModel.simulateManyTask()
    }
}
