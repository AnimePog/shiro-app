package com.lagradost.shiro.ui.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lagradost.shiro.utils.DataStore
import com.lagradost.shiro.R
import com.lagradost.shiro.utils.AppApi.init
import com.lagradost.shiro.utils.DownloadManager
import com.lagradost.shiro.utils.ShiroApi
import kotlin.concurrent.thread

/**
 * Loads [MainFragmentTV].
 */
class TvActivity : FragmentActivity() {
    companion object {
        var tvActivity: FragmentActivity? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ------ Init -----
        tvActivity = this
        DataStore.init(this)
        DownloadManager.init(this)
        init()
        thread {
            ShiroApi.init()
        }
        // ----- Theme -----
        theme.applyStyle(R.style.AppTheme, true)
        // -----------------

        setContentView(R.layout.activity_tv)
    }

    override fun onResume() {
        super.onResume()
        // This is needed to avoid NPE crash due to missing context
        DataStore.init(this)
        DownloadManager.init(this)
        init()

    }
}