package com.airwallex.paymentacceptance.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
    }

    abstract fun initView()

    abstract fun initListener()

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

    fun showAlert(title: String, message: String) {
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

    fun setLoadingProgress(loading: Boolean) {
        if (loading) {
            startWait()
        } else {
            endWait()
        }
    }

    private fun startWait() {
        if (dialog?.isShowing == true) {
            return
        }
        if (!isFinishing) {
            try {
                dialog = Dialog(this).apply {
                    setContentView(R.layout.airwallex_loading)
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
