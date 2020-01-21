package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.paymentacceptance.PaymentData.paymentMethodType
import kotlinx.android.synthetic.main.activity_edit_shipping.toolbar
import kotlinx.android.synthetic.main.activity_payment_methods.*

class PaymentSelectMethodActivity : AppCompatActivity() {

    private var menu: Menu? = null

    companion object {

        const val PAYMENT_METHOD_TYPE = "PAYMENT_METHOD_TYPE"

        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, PaymentSelectMethodActivity::class.java),
                requestCode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        ivChecked.visibility =
            if (paymentMethodType == PaymentMethodType.WECHAT) View.VISIBLE else View.GONE

        rlWechatPay.setOnClickListener {
            if (ivChecked.visibility == View.VISIBLE) {
                ivChecked.visibility = View.GONE
                paymentMethodType = null
            } else {
                ivChecked.visibility = View.VISIBLE
                paymentMethodType = PaymentMethodType.WECHAT
            }

            menu?.findItem(R.id.menu_save)?.isEnabled = paymentMethodType != null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
        menu?.findItem(R.id.menu_save)?.isEnabled = paymentMethodType != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> {
                val intent = Intent()
                intent.putExtra(PAYMENT_METHOD_TYPE, paymentMethodType)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}