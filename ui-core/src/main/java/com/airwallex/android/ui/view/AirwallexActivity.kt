package com.airwallex.android.ui.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewStub
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.airwallex.android.core.log.Logger
import com.airwallex.android.ui.R
import com.airwallex.android.ui.databinding.ActivityAirwallexBinding

abstract class AirwallexActivity : AppCompatActivity() {

    private val viewBinding: ActivityAirwallexBinding by lazy {
        ActivityAirwallexBinding.inflate(layoutInflater)
    }

    val viewStub: ViewStub by lazy {
        viewBinding.viewStub
    }

    val toolbar: Toolbar by lazy {
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

        Logger.debug("$localClassName#onCreate()")
    }

    override fun onDestroy() {
        Logger.debug("$localClassName#onDestroy()")
        super.onDestroy()
    }

    protected abstract fun onActionSave()

    @DrawableRes
    protected abstract fun homeAsUpIndicatorResId(): Int

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Logger.debug("$localClassName#onCreateOptionsMenu()")
        return if (item.itemId == R.id.action_save) {
            onActionSave()
            true
        } else {
            val handled = super.onOptionsItemSelected(item)
            if (!handled) {
                onBackPressed()
            }
            handled
        }
    }

    fun alert(title: String = "", message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }

    protected open fun setLoadingProgress(loading: Boolean, cancelable: Boolean = true) {
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
                    setCancelable(cancelable)
                    show()
                }
            } catch (e: Exception) {
                Logger.debug("Failed to show loading dialog", e)
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
