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
import com.airwallex.android.model.Product
import kotlinx.android.synthetic.main.cart_summary_item.view.*
import kotlinx.android.synthetic.main.fragment_order_summary.*

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
            R.layout.fragment_order_summary, null
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shippingItemView.renewalShipping(SampleApplication.instance.shipping)
        refreshProducts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shippingItemView.onActivityResult(requestCode, resultCode, data) {
            SampleApplication.instance.shipping = it
        }
    }

    private fun refreshProducts() {
        val products = SampleApplication.instance.products
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