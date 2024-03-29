package com.airwallex.paymentacceptance

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.core.Airwallex.Companion.AIRWALLEX_CHECKOUT_SCHEMA
import com.airwallex.paymentacceptance.databinding.ActivityPaymentCartBinding
import com.airwallex.paymentacceptance.h5.H5DemoActivity
import com.airwallex.paymentacceptance.wechat.WeChatDemoActivity

class PaymentCartActivity : AppCompatActivity() {

    private val viewBinding: ActivityPaymentCartBinding by lazy {
        ActivityPaymentCartBinding.inflate(layoutInflater)
    }

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container,
                    PaymentCartFragment()
                )
                .commit()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.scheme == AIRWALLEX_CHECKOUT_SCHEMA) {
            showAlert(
                getString(R.string.payment_successful),
                getString(R.string.payment_successful_message)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, PaymentSettingsActivity::class.java))
                true
            }
            R.id.h5demo -> {
                startActivity(Intent(this, H5DemoActivity::class.java))
                true
            }
            R.id.weChat_demo -> {
                startActivity(Intent(this, WeChatDemoActivity::class.java))
                true
            }
            else -> false
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

    companion object {
        private const val TAG = "PaymentCartActivity"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PaymentCartActivity::class.java))
            (context as Activity).finish()
        }
    }
}
