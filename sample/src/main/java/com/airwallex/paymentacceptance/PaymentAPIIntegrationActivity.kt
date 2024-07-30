package com.airwallex.paymentacceptance

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.databinding.ActivityApiIntegrationBinding

class PaymentAPIIntegrationActivity : AppCompatActivity() {

    private val viewBinding: ActivityApiIntegrationBinding by lazy {
        ActivityApiIntegrationBinding.inflate(layoutInflater)
    }

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initView()
        initListener()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    private fun initListener() {
        viewBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.radioPayment -> "Payment"
                R.id.radioRecurring -> "Recurring"
                R.id.radioRecurringAndPayment -> "Recurring and Payment"
                else -> "None"
            }
            Toast.makeText(this, "Changed selection: $selectedOption", Toast.LENGTH_SHORT).show()
        }
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
                Log.d(TAG, "Failed to show loading dialog", e)
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

    companion object {
        private const val TAG = "UIIntegrationActivity"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PaymentAPIIntegrationActivity::class.java))
        }
    }
}
