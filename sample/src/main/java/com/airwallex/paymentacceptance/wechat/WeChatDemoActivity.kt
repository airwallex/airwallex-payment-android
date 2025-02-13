package com.airwallex.paymentacceptance.wechat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
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

        viewBinding.flArrow.setOnClickListener { finish() }
        viewBinding.buttonNext.setOnSingleClickListener {

            if (viewBinding.partnerId.text.toString().isEmpty()) {
                showPaymentError("Missing PartnerId")
                return@setOnSingleClickListener
            }

            if (viewBinding.prepayId.text.toString().isEmpty()) {
                showPaymentError("Missing PrepayId")
                return@setOnSingleClickListener
            }

            if (viewBinding.packageStr.text.toString().isEmpty()) {
                showPaymentError("Missing Package")
                return@setOnSingleClickListener
            }

            if (viewBinding.nonceStr.text.toString().isEmpty()) {
                showPaymentError("Missing NonceStr")
                return@setOnSingleClickListener
            }

            if (viewBinding.timestamp.text.toString().isEmpty()) {
                showPaymentError("Missing Timestamp")
                return@setOnSingleClickListener
            }

            if (viewBinding.sign.text.toString().isEmpty()) {
                showPaymentError("Missing Sign")
                return@setOnSingleClickListener
            }

//            val weChat = WeChat(
//                appId = viewBinding.appId.text.toString(),
//                partnerId = viewBinding.partnerId.text.toString(),
//                prepayId = viewBinding.prepayId.text.toString(),
//                `package` = viewBinding.packageStr.text.toString(),
//                nonceStr = viewBinding.nonceStr.text.toString(),
//                timestamp = viewBinding.timestamp.text.toString(),
//                sign = viewBinding.sign.text.toString()
//            )
//
//            WXPay.instance.launchWeChat(
//                data = weChat,
//                listener = object : WXPay.WeChatPaymentListener {
//                    override fun onSuccess() {
//                        Log.d(TAG, "WeChat Pay successful.")
//                        showPaymentSuccess()
//                    }
//
//                    override fun onFailure(
//                        errCode: String?,
//                        errMessage: String?
//                    ) {
//                        Log.e(TAG, "WeChat Pay failed, reason: $errMessage")
//                        showPaymentError("WeChat Pay failed. message: $errMessage")
//                    }
//
//                    override fun onCancel() {
//                        Log.d(TAG, "WeChat Pay cancelled.")
//                        showPaymentError("WeChat Pay cancelled.")
//                    }
//                }
//            )
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
            .setPositiveButton(R.string.airwallex_okay) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
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
        const val TAG = "WeChatDemoActivity"
    }

}
