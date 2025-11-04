package com.airwallex.android.ui

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewStub
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.ui.databinding.ActivityAirwallexBinding

abstract class AirwallexActivity : AppCompatActivity() {

    private val viewBinding: ActivityAirwallexBinding by lazy {
        ActivityAirwallexBinding.inflate(layoutInflater)
    }

    val viewStub: ViewStub by lazy {
        viewBinding.viewStub
    }

    private val toolbar: Toolbar by lazy {
        viewBinding.toolbar
    }

    val loading: Boolean
        get() {
            return loadingDialog?.isShowing == true
        }

    private var loadingDialog: Dialog? = null
    private var isLoadingBeforeConfigChange = false
    private var loadingCancelable = true

    companion object {
        private const val KEY_IS_LOADING = "airwallex_is_loading"
        private const val KEY_LOADING_CANCELABLE = "airwallex_loading_cancelable"
    }

    abstract fun onBackButtonPressed()

    open fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorResId())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        AirwallexLogger.debug("$localClassName#onCreate()")
        if (this is TrackablePage) {
            AnalyticsLogger.logPageView(pageName, additionalInfo)
        }
    }

    open fun addListener() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackButtonPressed()
                }
            }
        )
    }

    open fun addObserver() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initView()
        addListener()
        addObserver()

        // Restore loading dialog state after configuration change
        savedInstanceState?.let {
            isLoadingBeforeConfigChange = it.getBoolean(KEY_IS_LOADING, false)
            loadingCancelable = it.getBoolean(KEY_LOADING_CANCELABLE, true)
            if (isLoadingBeforeConfigChange) {
                setLoadingProgress(loading = true, cancelable = loadingCancelable)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save loading dialog state before configuration change
        outState.putBoolean(KEY_IS_LOADING, loading)
        outState.putBoolean(KEY_LOADING_CANCELABLE, loadingCancelable)
    }

    override fun onDestroy() {
        AirwallexLogger.debug("$localClassName#onDestroy()")
        super.onDestroy()
    }

    @DrawableRes
    protected abstract fun homeAsUpIndicatorResId(): Int

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        AirwallexLogger.debug("$localClassName#onCreateOptionsMenu()")
        val handled = super.onOptionsItemSelected(item)
        if (!handled) {
            onBackPressedDispatcher.onBackPressed()
        }
        return handled
    }

    fun alert(title: String = "", message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.airwallex_okay) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }

    open fun setLoadingProgress(loading: Boolean, cancelable: Boolean = true) {
        loadingCancelable = cancelable
        if (loading) {
            startWait(this, cancelable)
        } else {
            endWait()
        }
    }

    private fun startWait(activity: Activity, cancelable: Boolean) {
        if (loadingDialog?.isShowing == true) {
            return
        }
        if (!activity.isFinishing) {
            try {
                loadingDialog = Dialog(activity).apply {
                    setContentView(R.layout.airwallex_loading)
                    window?.apply {
                        setBackgroundDrawableResource(android.R.color.transparent)
                        // Clear any dim behind
                        setDimAmount(0f)
                    }
                    setCancelable(cancelable)
                    show()
                }
            } catch (e: Exception) {
                AirwallexLogger.error("Failed to show loading dialog", e)
            }
        } else {
            loadingDialog = null
        }
    }

    private fun endWait() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}
