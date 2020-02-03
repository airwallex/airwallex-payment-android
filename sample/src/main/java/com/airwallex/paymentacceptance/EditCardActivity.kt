package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_add_card.shippingWidget
import kotlinx.android.synthetic.main.activity_add_card.toolbar
import okhttp3.ResponseBody
import java.util.*

class EditCardActivity : AppCompatActivity() {

    private val compositeSubscription = CompositeDisposable()
    private var menu: Menu? = null

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
    }

    companion object {
        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, EditCardActivity::class.java),
                requestCode
            )
        }
    }

    private fun updateMenuStatus() {
        menu?.findItem(R.id.menu_save)?.isEnabled =
            cardWidget.isValidCard && shippingWidget.isValidShipping
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        cardWidget.cardChangeCallback = {
            updateMenuStatus()
        }

        cardWidget.completionCallback = {
            shippingWidget.requestFocus()
        }

        shippingWidget.shippingChangeCallback = {
            updateMenuStatus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
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
        val card = cardWidget.paymentMethodCard ?: return
        val shipping = PaymentData.shipping ?: return
        compositeSubscription.add(
            api.savePaymentMethod(
                authorization = "Bearer ${Store.token}",
                params = mutableMapOf(
                    "request_id" to UUID.randomUUID().toString(),
                    "type" to "card",
                    "card" to card,
                    "billing" to shipping
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse(it) },
                    { handleError(it) }
                )
        )
    }

    private fun handleError(err: Throwable) {
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(responseBody: ResponseBody) {

    }
}