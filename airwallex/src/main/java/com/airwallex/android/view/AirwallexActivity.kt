package com.airwallex.android.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.support.v7.app.AppCompatActivity
import com.airwallex.android.Logger
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_airwallex.*

internal abstract class AirwallexActivity : AppCompatActivity() {

    val loading: Boolean
        get() {
            return loadingDialog?.isShowing == true
        }

    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airwallex)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorResId())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewStub.layoutResource = layoutResource
        viewStub.inflate()

        Logger.debug("$localClassName#onCreate()")
    }

    override fun onDestroy() {
        Logger.debug("$localClassName#onDestroy()")
        super.onDestroy()
    }

    protected abstract val layoutResource: Int

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

    internal fun alert(title: String = "", message: String) {
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
