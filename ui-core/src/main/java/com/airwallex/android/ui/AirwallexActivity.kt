package com.airwallex.android.ui

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.airwallex.android.core.log.ConsoleLogger
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorResId())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ConsoleLogger.debug("$localClassName#onCreate()")
    }

    override fun onDestroy() {
        ConsoleLogger.debug("$localClassName#onDestroy()")
        super.onDestroy()
    }

    @DrawableRes
    protected abstract fun homeAsUpIndicatorResId(): Int

    override fun onBackPressed() {
        val container = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        if (container.childCount > 0 && container.getChildAt(container.childCount - 1) is AirwallexWebView) {
            (container.getChildAt(container.childCount - 1) as AirwallexWebView).destroyWebView()
            return
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        ConsoleLogger.debug("$localClassName#onCreateOptionsMenu()")
        val handled = super.onOptionsItemSelected(item)
        if (!handled) {
            onBackPressed()
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
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    setCancelable(cancelable)
                    show()
                }
            } catch (e: Exception) {
                ConsoleLogger.debug("Failed to show loading dialog", e)
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
