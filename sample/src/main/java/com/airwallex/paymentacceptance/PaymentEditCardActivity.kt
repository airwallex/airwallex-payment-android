package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_add_card.toolbar
import java.util.*

class PaymentEditCardActivity : PaymentBaseActivity() {

    private val compositeSubscription = CompositeDisposable()
    private var menu: Menu? = null

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
    }

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

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    @Throws(IllegalArgumentException::class)
    private fun actionSave() {
        loading.visibility = View.VISIBLE
        val card = cardWidget.paymentMethodCard ?: return
        compositeSubscription.add(
            api.savePaymentMethod(
                authorization = "Bearer ${Store.token}",
                params = mutableMapOf(
                    "request_id" to UUID.randomUUID().toString(),
                    "type" to "card",
                    "card" to card,
                    "billing" to billingWidget.billing
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse() },
                    { handleError(it) }
                )
        )
    }

    private fun handleError(err: Throwable) {
        loading.visibility = View.GONE
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse() {
        loading.visibility = View.GONE
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}