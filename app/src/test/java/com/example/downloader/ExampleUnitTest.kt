package com.example.downloader

import com.example.downloader.base.BaseInstantTest
import io.reactivex.BackpressureOverflowStrategy
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.*
import org.reactivestreams.Subscription
import timber.log.Timber

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest : BaseInstantTest(){
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun simulateManyTask() {
        val intArray = IntArray(200) {it}.toTypedArray().toList()
        var sub: Subscription? = null
        Flowable.fromIterable(intArray)
            .subscribeOn(Schedulers.io())
            .onBackpressureBuffer(10,
                { Timber.d("on overflow, time ${System.currentTimeMillis()}") },
                BackpressureOverflowStrategy.DROP_LATEST)
            .doOnNext {
                Timber.d("on sleep $it")
                Thread.sleep(100)
            }
            .observeOn(Schedulers.newThread(), true, 1)
            .subscribe({
                Timber.d("on next $it")
                sub?.request(1)
            }, {
                Timber.d("on error $it")
            }, {
                Timber.d("on done")
            }, {
                Timber.d("on sub")
                sub = it
                sub?.request(3)
            })
    }

}
