package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_shipping.toolbar
import kotlinx.android.synthetic.main.activity_payment_methods.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class PaymentMethodsActivity : PaymentBaseActivity() {

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
    }

    val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    private val compositeSubscription = CompositeDisposable()
    private lateinit var cardAdapter: PaymentMethodsAdapter
    private val paymentMethods = mutableListOf<PaymentMethod?>(null, null)

    companion object {
        fun startActivityForResult(
            activity: Activity,
            paymentMethod: PaymentMethod?,
            paymentIntentId: String,
            requestCode: Int
        ) {
            val intent = Intent(activity, PaymentMethodsActivity::class.java)
            intent.putExtra(PAYMENT_METHOD, paymentMethod)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            activity.startActivityForResult(intent, requestCode)
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

        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cardAdapter = PaymentMethodsAdapter(
            paymentMethods,
            this,
            intent.getParcelableExtra(PAYMENT_METHOD) as? PaymentMethod
        )
        rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = cardAdapter
        }

        fetchPaymentMethods()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_save)?.isEnabled = cardAdapter.paymentMethod != null
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_payment_method, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> {
                val intent = Intent()
                intent.putExtra(PAYMENT_METHOD, cardAdapter.paymentMethod)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    private fun fetchPaymentMethods() {
        compositeSubscription.add(
            // TODO Visa & Mastercard
            api.fetchPaymentMethods(authorization = "Bearer ${Store.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponse(it) },
                    { handleError(it) }
                )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_EDIT_CARD_CODE -> {
                fetchPaymentMethods()
            }
        }
    }

    private fun handleError(err: Throwable) {
        Toast.makeText(this, err.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleResponse(responseBody: ResponseBody) {
        try {
            val responseData = JSONObject(responseBody.string())
            val items: List<PaymentMethod> = Gson().fromJson(
                responseData["items"].toString(),
                object : TypeToken<List<PaymentMethod?>?>() {}.type
            )

            paymentMethods.clear()
            paymentMethods.add(null)
            paymentMethods.addAll(items.filter { it.type == PaymentMethodType.CARD })
            paymentMethods.add(null)
            cardAdapter.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}