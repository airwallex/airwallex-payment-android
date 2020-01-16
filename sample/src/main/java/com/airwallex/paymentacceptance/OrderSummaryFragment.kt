package com.airwallex.paymentacceptance

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.airwallex.paymentacceptance.model.Product
import kotlinx.android.synthetic.main.fragment_order_summary.*
import kotlinx.android.synthetic.main.order_summary_item.view.*

class OrderSummaryFragment : Fragment() {

    @SuppressLint("ViewConstructor")
    class OrderSummaryItem(val order: Product, context: Context?, val removeHandler: () -> Unit) :
        RelativeLayout(context) {

        init {
            View.inflate(context,
                R.layout.order_summary_item, this)

            tvProductName.text = order.name
            tvProductType.text = order.type + " * " + order.quantity
            tvProductPrice.text = (order.unitPrice * order.quantity).toString()
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
        return View.inflate(context,
            R.layout.fragment_order_summary, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshUI()
    }

    private fun refreshUI() {
        val products = TestData.products
        llProducts.removeAllViews()
        products.map {
            OrderSummaryItem(
                it,
                context
            ) {
                products.remove(it)
                refreshUI()
            }
        }.forEach { llProducts.addView(it) }

        val subtotalPrice = products.sumBy { it.unitPrice * it.quantity }
        val shipping = 0
        val totalPrice = subtotalPrice + shipping

        tvOrderSubtotalPrice.text = subtotalPrice.toString()
        tvOrderTotalPrice.text = totalPrice.toString()
    }
}