package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_add_card.toolbar
import java.util.*

class PaymentEditCardActivity : PaymentBaseActivity() {

    private var menu: Menu? = null

    companion object {
        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, PaymentEditCardActivity::class.java),
                requestCode
            )
        }
    }

    private fun updateMenuStatus() {
        menu?.findItem(R.id.menu_save)?.isEnabled =
            cardWidget.isValidCard && billingWidget.isValidBilling
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setTitle(R.string.cvc_hint)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        cardWidget.cardChangeCallback = {
            updateMenuStatus()
        }

        cardWidget.completionCallback = {
            billingWidget.requestFocus()
        }

        billingWidget.billingChangeCallback = {
            updateMenuStatus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
        updateMenuStatus()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> {
                actionSave()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @Throws(IllegalArgumentException::class)
    private fun actionSave() {
        loading.visibility = View.VISIBLE
        val card = cardWidget.paymentMethodCard ?: return

        val paymentMethodParams = PaymentMethodParams.Builder()
            .setRequestId(UUID.randomUUID().toString())
            .setType("card")
            .setCard(card)
            .setBilling(billingWidget.billing)
            .build()

        val airwallex = Airwallex(Store.token)
        airwallex.createPaymentMethod(
            paymentMethodParams,
            object : Airwallex.PaymentMethodCallback {
                override fun onSuccess(paymentMethod: PaymentMethod) {
                    loading.visibility = View.GONE
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    Toast.makeText(
                        this@PaymentEditCardActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            })
    }
}