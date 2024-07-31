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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.airwallex.paymentacceptance.R
import java.lang.reflect.ParameterizedType

abstract class BaseMvvmActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    open lateinit var mViewModel: VM
    open lateinit var mBinding: VB
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = getViewModel()!!
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

    protected open fun getViewModel(): VM? {
        val type = javaClass.genericSuperclass
        if (type != null && type is ParameterizedType) {
            val actualTypeArguments = type.actualTypeArguments
            val tClass = actualTypeArguments[1]
            return ViewModelProvider(this)[tClass as Class<VM>]
        }
        return null
    }

    fun setLoadingProgress(loading: Boolean) {
        if (loading) {
            startWait()
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

    private fun showAlert(title: String, message: String) {
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

    private fun startWait() {
        if (dialog?.isShowing == true) {
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
                    setCancelable(false)
                    show()
                }
            } catch (e: Exception) {
                Log.d("", "Failed to show loading dialog", e)
            }
        } else {
            dialog = null
        }
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
