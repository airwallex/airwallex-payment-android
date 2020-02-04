package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.model.PaymentMethod
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

class PaymentMethodsActivity : AppCompatActivity() {

    var menu: Menu? = null

    private val api: Api by lazy {
        ApiFactory(Constants.BASE_URL).create()
    }

    private val compositeSubscription = CompositeDisposable()
    private lateinit var cardAdapter: CardAdapter
    private val paymentMethods = mutableListOf<PaymentMethod?>(null, null)

    companion object {

        const val PAYMENT_METHOD = "PAYMENT_METHOD"

        const val REQUEST_EDIT_CARD_CODE = 9998

        fun startActivityForResult(
            activity: Activity,
            paymentMethod: PaymentMethod?,
            requestCode: Int
        ) {
            val intent = Intent(activity, PaymentMethodsActivity::class.java)
            intent.putExtra(PAYMENT_METHOD, paymentMethod)
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
        cardAdapter = CardAdapter(paymentMethods, this, intent.getParcelableExtra(PAYMENT_METHOD) as? PaymentMethod)
        rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = cardAdapter
        }

        fetchPaymentMethods()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
        menu?.findItem(R.id.menu_save)?.isEnabled = cardAdapter.paymentMethod != null
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
            api.fetchPaymentMethods(authorization = "Bearer ${Store.token}", method = "visa")
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
            paymentMethods.addAll(items)
            paymentMethods.add(null)
            cardAdapter.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}