package com.airwallex.paymentacceptance

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

class PaymentCartActivity : AppCompatActivity() {

    private val compositeSubscription = CompositeDisposable()

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
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
        var token = ""
        compositeSubscription.add(
            api.authentication(
                apiKey = Constants.API_KEY,
                clientId = Constants.CLIENT_ID
            )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe {
                    loading.visibility = View.VISIBLE
                }
                .flatMap {
                    val fgOrderSummary =
                        supportFragmentManager.findFragmentById(R.id.fgOrderSummary) as CartFragment
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
                                .setShipping(PaymentData.shipping)
                                .setType("physical_goods")
                                .build(),
                            "request_id" to UUID.randomUUID().toString()
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse(it, token) },
                    { handleError(it) }
                )
        )
    }

    private fun handleError(err: Throwable) {
        loading.visibility = View.GONE
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(responseBody: ResponseBody, token: String) {
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