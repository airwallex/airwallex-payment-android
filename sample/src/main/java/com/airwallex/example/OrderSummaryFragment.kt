package com.airwallex.example

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.airwallex.example.model.Product
import kotlinx.android.synthetic.main.fragment_order_summary.*
import kotlinx.android.synthetic.main.order_summary_item.view.*

class OrderSummaryFragment : Fragment() {


    private val products = mutableListOf(
        Product(
            code = 123,
            name = "IPhone XR",
            desc = "Buy iPhone XR, per month with trade-in",
            sku = "piece",
            type = "physical",
            unitPrice = 100,
            url = "www.aircross.com",
            quantity = 1
        ),
        Product(
            code = 123,
            name = "IPad Air 5",
            desc = "Buy iPad, Get free two-business-day delivery on any in‑stock iPad ordered by 5:00 p.m.",
            sku = "piece",
            type = "physical",
            unitPrice = 200,
            url = "www.aircross.com",
            quantity = 2
        ),
        Product(
            code = 123,
            name = "MacBook Pro",
            desc = "Buy iMac Pro. 3.2GHz 8-core Intel Xenon W processor",
            sku = "piece",
            type = "physical",
            unitPrice = 1000,
            url = "www.aircross.com",
            quantity = 8
        )
    )

    @SuppressLint("ViewConstructor")
    class OrderSummaryItem(val order: Product, context: Context?, val removeHandler: () -> Unit) :
        RelativeLayout(context) {

        init {
            View.inflate(context, R.layout.order_summary_item, this)

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
        return View.inflate(context, R.layout.fragment_order_summary, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()

        rlOrderSummaryHeader.setOnClickListener {
            llOrderSummaryContent.visibility =
                if (llOrderSummaryContent.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun updateUI() {
        llProducts.removeAllViews()
        products.map {
            OrderSummaryItem(it, context) {
                products.remove(it)
                updateUI()
            }
        }.forEach { llProducts.addView(it) }

        val subtotalPrice = products.sumBy { it.unitPrice * it.quantity }
        val shipping = 0
        val totalPrice = subtotalPrice + shipping

        tvOrderSubtotalPrice.text = subtotalPrice.toString()
        tvOrderHeaderTotalPrice.text = totalPrice.toString()
        tvOrderTotalPrice.text = totalPrice.toString()
    }
}