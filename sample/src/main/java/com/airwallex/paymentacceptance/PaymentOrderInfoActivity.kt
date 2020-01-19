package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.Order
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_payment_order_info.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class PaymentOrderInfoActivity : AppCompatActivity() {

    private val compositeSubscription = CompositeDisposable()

    private val api: Api by lazy {
        ApiFactory("https://staging-pci-api.airwallex.com").create()
    }

    private var token: String = ""

    companion object {

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, PaymentOrderInfoActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_order_info)

        btnCheckout.setOnClickListener {
            authAndCreatePaymentIntent()
        }
    }

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    private fun authAndCreatePaymentIntent() {
        compositeSubscription.add(
            api.authentication(
                apiKey = "9092eb393908b656c2ed8134535b574c30e7a243718a1c08a06b8ea9278919f4550af02cac520e062518028204c1dc54",
                clientId = "DW19XFSMSUq4YPc7xkM4Nw"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe {
                    loading.visibility = View.VISIBLE
                }
                .flatMap {
                    val fgOrderSummary =
                        supportFragmentManager.findFragmentById(R.id.fgOrderSummary) as OrderSummaryFragment
                    val responseData = JSONObject(it.string())
                    token = responseData["token"].toString()
                    api.createPaymentIntent(
                        authorization = "Bearer $token",
                        params = mutableMapOf(
                            "amount" to 0.01,
                            "currency" to "USD",
                            "descriptor" to "Airwallex - T-shirt",
                            "merchant_order_id" to UUID.randomUUID().toString(),
                            "metadata" to mapOf("id" to 1),
                            "order" to Order.Builder()
                                .setProducts(fgOrderSummary.products)
                                .setShipping(Data.shipping)
                                .setType("physical_goods")
                                .build(),
                            "request_id" to UUID.randomUUID().toString()
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse(it) },
                    { handleError(it) }
                )
        )
    }

    private fun handleError(err: Throwable) {
        loading.visibility = View.GONE
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(responseBody: ResponseBody) {
        loading.visibility = View.GONE
        try {
            val responseData = JSONObject(responseBody.string())
            PaymentStartPayActivity.start(
                this,
                responseData["id"].toString(),
                responseData["amount"].toString().toFloat(),
                token
            )
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}