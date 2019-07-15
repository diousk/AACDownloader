package com.example.downloader.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkManager
import com.example.downloader.R
import com.example.downloader.database.daos.ResourceDao
import com.example.downloader.observeOnce
import com.example.downloader.readFile
import com.example.downloader.worker.DownloadWorker
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var resourceDao: ResourceDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        download.setOnClickListener {
            //            val sampleUrl = Data.sampleUrls[(0..Data.sampleUrls.size).random()]
            val jsonData = assets.readFile("res_simple.json")
            viewModel.download(jsonData)
        }

        cancel.setOnClickListener {
            viewModel.cancel()
        }

        checkFinish.setOnClickListener {
            resourceDao.getAllResources()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Timber.d("resources = $it")
                }, {
                    Timber.d("get resources error = $it")
                })

            WorkManager.getInstance().getWorkInfosForUniqueWorkLiveData(DownloadWorker.TAG)
                .observeOnce(this, Observer {
                    Timber.d("worker list ${it.size}")
                    it.forEachIndexed { index, workInfo ->
                        Timber.d(
                            "worker: ${index}, " +
                                    "${workInfo.state}, " +
                                    "${workInfo.tags}"
                        )
                    }
                })
        }
    }
}
