package com.lagradost.shiro.ui.tv

import android.content.Intent
import android.os.Bundle
import android.view.FocusFinder
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.lagradost.shiro.R
import com.lagradost.shiro.ui.MainActivity.Companion.masterViewModel
import com.lagradost.shiro.ui.MasterViewModel
import com.lagradost.shiro.ui.home.ExpandedHomeFragment.Companion.isInExpandedView
import com.lagradost.shiro.ui.result.ResultFragment.Companion.isInResults
import com.lagradost.shiro.ui.settings.SettingsFragment.Companion.isInSettings
import com.lagradost.shiro.ui.tv.PlayerFragmentTv.Companion.isInPlayer
import com.lagradost.shiro.utils.*
import com.lagradost.shiro.utils.AniListApi.Companion.authenticateLogin
import com.lagradost.shiro.utils.AppUtils.init
import com.lagradost.shiro.utils.AppUtils.popCurrentPage
import com.lagradost.shiro.utils.AppUtils.settingsManager
import com.lagradost.shiro.utils.InAppUpdater.runAutoUpdate
import kotlinx.android.synthetic.main.activity_tv.*
import kotlinx.android.synthetic.main.fragment_main_tv.*
import kotlin.concurrent.thread

/**
 * Loads [MainFragment].
 */
class TvActivity : AppCompatActivity() {
    companion object {
        var tvActivity: AppCompatActivity? = null
        var isInSearch = false

        fun FragmentActivity.applyThemes() {
            // ----- Themes ----
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            //theme.applyStyle(R.style.AppTheme, true)

            val currentTheme = when (settingsManager!!.getString("theme", "Black")) {
                "Black" -> R.style.AppTheme
                "Dark" -> R.style.DarkMode
                "Light" -> R.style.LightMode
                else -> R.style.AppTheme
            }

            /*if (settingsManager.getBoolean("cool_mode", false)) {
                theme.applyStyle(R.style.OverlayPrimaryColorBlue, true)
            } else if (BuildConfig.BETA && settingsManager.getBoolean("beta_theme", false)) {
                theme.applyStyle(R.style.OverlayPrimaryColorGreen, true)
            }*/
            //theme.applyStyle(R.style.AppTheme, true)
            theme.applyStyle(R.style.Theme_LeanbackCustom, true)
            theme.applyStyle(currentTheme, true)
            AppUtils.getTheme()?.let {
                theme.applyStyle(it, true)
            }
            // -----------------
        }

    }

    override fun onBackPressed() {
        if ((isInSearch || isInSettings) && !isInResults) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.home_root_tv, MainFragment())
                .commit()

        } else if (isInResults || isInPlayer) {
            popCurrentPage(isInPlayer, isInExpandedView, isInResults)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        masterViewModel = masterViewModel ?: ViewModelProvider(this).get(MasterViewModel::class.java)
        DataStore.init(this)
        settingsManager = PreferenceManager.getDefaultSharedPreferences(this)

        applyThemes()
        super.onCreate(savedInstanceState)
        /*if (!isTv()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }*/
        // ------ Init -----
        tvActivity = this
        DownloadManager.init(this)
        init()
        thread {
            ShiroApi.init()
        }
        thread {
            runAutoUpdate(this)
        }

        setContentView(R.layout.activity_tv)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_DPAD_UP && !isInPlayer && !isInResults) {
            try {
                val nextFocused =
                    FocusFinder.getInstance().findNextFocus(home_root_tv, currentFocus, View.FOCUS_UP)
                if (nextFocused == null) {
                    //println("Null focus")
                    search_icon.requestFocus()
                } else {
                    //println("Found focus")
                    nextFocused.requestFocus()
                    //super.onKeyDown(keyCode, event)
                }
            } catch (e: Exception) {
                return false
            }
        } else {
            //println("Not")
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onResume() {
        super.onResume()
        // This is needed to avoid NPE crash due to missing context
        DataStore.init(this)
        DownloadManager.init(this)
        init()

    }


    // AUTH FOR LOGIN
    override fun onNewIntent(intent: Intent?) {
        if (intent != null) {
            val dataString = intent.dataString
            if (dataString != null && dataString != "") {
                if (dataString.contains("shiroapp")) {
                    if (dataString.contains("/anilistlogin")) {
                        authenticateLogin(dataString)
                    } else if (dataString.contains("/mallogin")) {
                        MALApi.authenticateLogin(dataString)
                    }
                }
            }
        }

        super.onNewIntent(intent)
    }

}