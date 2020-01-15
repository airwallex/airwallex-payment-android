package com.airwallex.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.PaymentConfiguration
import com.airwallex.example.model.Order
import com.airwallex.example.model.Product
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
        ApiFactory(PaymentConfiguration.getInstance(applicationContext).environment.baseUrl).create()
    }

    companion object {

        val TAG = PaymentOrderInfoActivity::class.java.canonicalName

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
                apiKey = PaymentConfiguration.getInstance(applicationContext).apiKey,
                clientId = PaymentConfiguration.getInstance(applicationContext).clientId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe {
                    loading.visibility = View.VISIBLE
                }
                .flatMap {
                    val responseData = JSONObject(it.string())
                    api.createPaymentIntent(
                        authorization = "Bearer " + responseData["token"],
                        params = mutableMapOf(
                            "amount" to 100.01,
                            "currency" to "USD",
                            "descriptor" to "Airwallex - T-shirt",
                            "merchant_order_id" to UUID.randomUUID().toString(),
                            "metadata" to mapOf("id" to 1),
                            "order" to Order(
                                products = TestData.products,
                                shipping = TestData.shipping,
                                type = "physical_goods"
                            ),

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
            Log.e("aaa", "responseData $responseData")

            PaymentStartPayActivity.start(this)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}