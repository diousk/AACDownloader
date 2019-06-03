package com.example.downloader.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kingkonglive.android.base.RxSchedulerRule
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.Rule

open class BaseInstantTest {
    @get:Rule // for rx to run synchronously
    val rxSchedulerRule = RxSchedulerRule()
    @get:Rule // for live data
    var liveDataRule = InstantTaskExecutorRule()
    @get:Rule // for timber to log
    var logAllAlwaysRule = TimberTestRule.logAllAlways()
}