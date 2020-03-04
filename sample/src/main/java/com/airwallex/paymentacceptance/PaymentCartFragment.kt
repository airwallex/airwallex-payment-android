package com.airwallex.paymentacceptance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.airwallex.android.model.Address
import com.airwallex.android.model.Product
import com.airwallex.android.model.Shipping
import kotlinx.android.synthetic.main.cart_summary_item.view.*
import kotlinx.android.synthetic.main.fragment_cart_summary.*

class PaymentCartFragment : Fragment() {

    var shipping: Shipping = Shipping.Builder()
        .setFirstName("John")
        .setLastName("Doe")
        .setPhone("13800000000")
        .setAddress(
            Address.Builder()
                .setCountryCode("CN")
                .setState("Shanghai")
                .setCity("Shanghai")
                .setStreet("Pudong District")
                .setPostcode("100000")
                .build()
        )
        .build()

    val products = mutableListOf(
        Product.Builder()
            .setCode("123")
            .setName("AirPods Pro")
            .setDesc("Buy AirPods Pro, per month with trade-in")
            .setSku("piece")
            .setType("Free engraving")
            .setUnitPrice(399.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build(),
        Product.Builder()
            .setCode("123")
            .setName("HomePod")
            .setDesc("Buy HomePod, per month with trade-in")
            .setSku("piece")
            .setType("White")
            .setUnitPrice(469.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build()
    )

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
                R.layout.cart_summary_item, this
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
            R.layout.fragment_cart_summary, null
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shippingItemView.renewalShipping(shipping)
        refreshProducts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shippingItemView.onActivityResult(requestCode, resultCode, data) {
            it?.let {
                shipping = it
            }
        }
    }

    private fun refreshProducts() {
        val products = products
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