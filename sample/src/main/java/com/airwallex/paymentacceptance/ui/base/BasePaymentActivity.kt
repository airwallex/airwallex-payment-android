package com.airwallex.paymentacceptance.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel

abstract class BasePaymentActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    open lateinit var mViewModel: VM
    open lateinit var mBinding: VB
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[getViewModelClass()]
        mViewModel.init(this)
        mBinding = getViewBinding()
        setContentView(mBinding.root)
        initView()
        initListener()
        addObserver()
    }

    abstract fun initView()

    abstract fun initListener()

    abstract fun addObserver()

    abstract fun getViewBinding(): VB

    abstract fun getViewModelClass(): Class<VM>

    fun setLoadingProgress(loading: Boolean, cancellable: Boolean = false) {
        if (loading) {
            startWait(cancellable)
        } else {
            endWait()
        }
    }

    fun showPaymentSuccess() {
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        )
    }

    fun showCreatePaymentIntentError(error: String? = null) {
        showAlert(
            getString(R.string.create_payment_intent_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    fun showPaymentError(error: String? = null) {
        showAlert(
            getString(R.string.payment_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    fun showPaymentCancelled(error: String? = null) {
        showAlert(
            getString(R.string.payment_cancelled),
            error ?: getString(R.string.payment_cancelled_message)
        )
    }

    fun showPaymentInProgress() {
    }

    fun showAlert(title: String, message: String, callback: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
                callback?.invoke()
            }
            .create()
            .show()
    }

    private fun startWait(cancellable: Boolean = false) {
        // If dialog already showing, just update cancelable state (reentrant)
        if (dialog?.isShowing == true) {
            dialog?.setCancelable(cancellable)
            if (cancellable) {
                dialog?.setOnCancelListener { onLoadingCancelled() }
            } else {
                dialog?.setOnCancelListener(null)
            }
            return
        }
        if (!isFinishing) {
            try {
                dialog = Dialog(this).apply {
                    setContentView(R.layout.airwallex_loading)
                    val progressBar = findViewById<ProgressBar>(R.id.airwallex_progress_bar)
                    progressBar.indeterminateTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.color_primary
                        )
                    )
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    setCancelable(cancellable)
                    if (cancellable) {
                        setOnCancelListener { onLoadingCancelled() }
                    }
                    show()
                }
            } catch (e: Exception) {
                Log.d("", "Failed to show loading dialog", e)
            }
        } else {
            dialog = null
        }
    }

    /**
     * Called when user presses back while loading dialog is showing.
     * Override in subclasses to handle cancellation (e.g., stop polling, finish activity).
     */
    open fun onLoadingCancelled() {
        mViewModel.stopPolling()
    }

    private fun endWait() {
        dialog?.dismiss()
        dialog = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
