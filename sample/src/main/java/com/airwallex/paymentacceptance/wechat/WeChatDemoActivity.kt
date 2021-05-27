package com.airwallex.paymentacceptance.wechat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.WeChat
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.WXPay
import com.airwallex.paymentacceptance.databinding.ActivityWechatDemoBinding

class WeChatDemoActivity : AppCompatActivity() {

    private val viewBinding: ActivityWechatDemoBinding by lazy {
        ActivityWechatDemoBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.wechat_demo)

        viewBinding.appId.setText(Settings.weChatAppId)
        viewBinding.partnerId.setText("353449704")
        viewBinding.packageStr.setText("Sign=WXPay")

        viewBinding.buttonNext.setOnClickListener {

            if (viewBinding.partnerId.text.toString().isEmpty()) {
                showPaymentError("Missing PartnerId")
                return@setOnClickListener
            }

            if (viewBinding.prepayId.text.toString().isEmpty()) {
                showPaymentError("Missing PrepayId")
                return@setOnClickListener
            }

            if (viewBinding.packageStr.text.toString().isEmpty()) {
                showPaymentError("Missing Package")
                return@setOnClickListener
            }

            if (viewBinding.nonceStr.text.toString().isEmpty()) {
                showPaymentError("Missing NonceStr")
                return@setOnClickListener
            }

            if (viewBinding.timestamp.text.toString().isEmpty()) {
                showPaymentError("Missing Timestamp")
                return@setOnClickListener
            }

            if (viewBinding.sign.text.toString().isEmpty()) {
                showPaymentError("Missing Sign")
                return@setOnClickListener
            }

            val weChat = WeChat(
                appId = viewBinding.appId.text.toString(),
                partnerId = viewBinding.partnerId.text.toString(),
                prepayId = viewBinding.prepayId.text.toString(),
                `package` = viewBinding.packageStr.text.toString(),
                nonceStr = viewBinding.nonceStr.text.toString(),
                timestamp = viewBinding.timestamp.text.toString(),
                sign = viewBinding.sign.text.toString()
            )

            WXPay.instance.launchWeChat(
                data = weChat,
                listener = object : WXPay.WeChatPaymentListener {
                    override fun onSuccess() {
                        Log.d(TAG, "WeChat Pay successful.")
                        showPaymentSuccess()
                    }

                    override fun onFailure(
                        errCode: String?,
                        errMessage: String?
                    ) {
                        Log.e(TAG, "WeChat Pay failed, reason: $errMessage")
                        showPaymentError("WeChat Pay failed. message: $errMessage")
                    }

                    override fun onCancel() {
                        Log.d(TAG, "WeChat Pay cancelled.")
                        showPaymentError("WeChat Pay cancelled.")
                    }
                }
            )
        }
    }

    private fun showPaymentSuccess() {
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        )
    }

    private fun showPaymentError(error: String? = null) {
        showAlert(
            getString(R.string.payment_failed),
            error ?: getString(R.string.payment_failed_message)
        )
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "WeChatDemoActivity"
    }
}
