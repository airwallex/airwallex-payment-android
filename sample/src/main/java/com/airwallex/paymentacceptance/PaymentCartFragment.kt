package com.airwallex.paymentacceptance

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airwallex.android.model.Product
import com.airwallex.android.model.Shipping
import kotlinx.android.synthetic.main.fragment_order_summary.*
import kotlinx.android.synthetic.main.order_summary_item.view.*
import java.util.*

class PaymentCartFragment : Fragment() {

    @SuppressLint("ViewConstructor")
    class OrderSummaryItem(
        order: Product,
        context: Context?,
        private val removeHandler: () -> Unit
    ) :
        RelativeLayout(context) {

        init {
            View.inflate(
                context,
                R.layout.order_summary_item, this
            )

            tvProductName.text = order.name
            tvProductType.text = String.format("%s x %d", order.type, order.quantity)
            tvProductPrice.text =
                String.format("$%.2f", order.unitPrice ?: 0 * (order.quantity ?: 0))
            tvRemove.setOnClickListener {
                removeHandler.invoke()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(
            context,
            R.layout.fragment_order_summary, null
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shipping = (context as PaymentCartActivity).shipping
        rlShipping.setOnClickListener {
            activity?.let {
                PaymentEditShippingActivity.startActivityForResult(
                    it,
                    shipping,
                    PaymentBaseActivity.REQUEST_EDIT_SHIPPING_CODE
                )
            }
        }

        refreshShippingAddress(shipping)
        refreshProducts()
    }

    private fun refreshShippingAddress(shipping: Shipping?) {
        if (shipping == null) {
            tvShippingAddress.text = getString(R.string.select_shipping)
            tvShippingAddress.setTextColor(Color.parseColor("#A9A9A9"))
            return
        }

        val countryName = shipping.address?.countryCode?.let {
            val loc = Locale("", it)
            loc.displayCountry
        }

        tvShippingAddress.text = String.format(
            "%s %s\n%s\n%s, %s, %s",
            shipping.lastName,
            shipping.firstName,
            shipping.address?.street,
            shipping.address?.city,
            shipping.address?.state,
            countryName
        )

        context?.let {
            tvShipping.setTextColor(ContextCompat.getColor(it, R.color.airwallex_dark_gray))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            PaymentBaseActivity.REQUEST_EDIT_SHIPPING_CODE -> {
                val shipping =
                    data.getParcelableExtra<Parcelable>(PaymentBaseActivity.SHIPPING_DETAIL) as Shipping
                refreshShippingAddress(shipping)
                (context as PaymentCartActivity).shipping = shipping
            }
        }
    }

    private fun refreshProducts() {
        val products = (context as PaymentCartActivity).products
        llProducts.removeAllViews()
        products.map {
            OrderSummaryItem(
                it,
                context
            ) {
                products.remove(it)
                refreshProducts()
            }
        }.forEach { llProducts.addView(it) }

        val subtotalPrice =
            products.sumByDouble { it.unitPrice ?: 0 * (it.quantity ?: 0).toDouble() }
        val shipping = 0
        val totalPrice = subtotalPrice + shipping

        tvOrderSubtotalPrice.text = String.format("$%.2f", subtotalPrice)
        tvOrderTotalPrice.text = String.format("$%.2f", totalPrice)
        tvShipping.text = getString(R.string.free)
        tvOrderSum.text = products.sumBy { it.quantity ?: 0 }.toString()
    }
}