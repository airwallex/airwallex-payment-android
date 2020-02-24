package com.airwallex.android.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.R
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.model.WechatPayFlow
import com.airwallex.android.model.WechatPayFlowType
import kotlinx.android.synthetic.main.payment_method_item_add_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_wechat.view.*
import java.util.*

internal class PaymentMethodsAdapter(
    var selectedPaymentMethod: PaymentMethod?,
    shouldShowWechatPay: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val addPaymentCardCount = 1
    private val wechatCount = if (shouldShowWechatPay) 1 else 0

    internal val paymentMethods = ArrayList<PaymentMethod>()

    internal var callback: Callback? = null

    internal fun setPaymentMethods(paymentMethods: List<PaymentMethod>) {
        this.paymentMethods.addAll(0, paymentMethods)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return paymentMethods.size + addPaymentCardCount + wechatCount
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isAddPaymentCardPosition(position) -> ItemViewType.AddCard.ordinal
            isPaymentMethodsPosition(position) -> ItemViewType.Card.ordinal
            else -> ItemViewType.Wechat.ordinal
        }
    }

    private fun isAddPaymentCardPosition(position: Int): Boolean {
        return position == paymentMethods.size
    }

    private fun isPaymentMethodsPosition(position: Int): Boolean {
        return position < paymentMethods.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.Card -> CardHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_card, parent, false)
            )
            ItemViewType.AddCard -> AddCardHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_add_card, parent, false)
            )
            ItemViewType.Wechat -> WechatHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_wechat, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WechatHolder -> holder.bindView()
            is CardHolder -> holder.bindView(position)
            is AddCardHolder -> holder.bindView()
        }
    }

    inner class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("DefaultLocale")
        fun bindView(position: Int) {
            val method = paymentMethods[position]
            val card = method.card ?: return
            itemView.tvCardInfo.text =
                String.format("%s •••• %s", card.brand?.capitalize(), card.last4)
            when (card.brand) {
                CardBrand.Visa.type -> itemView.ivCardIcon.setImageResource(R.drawable.airwallex_ic_visa)
                CardBrand.MasterCard.type -> itemView.ivCardIcon.setImageResource(R.drawable.airwallex_ic_mastercard)
            }
            itemView.rlCard.setOnClickListener {
                if (selectedPaymentMethod?.type != PaymentMethodType.CARD || method.id != selectedPaymentMethod?.id) {
                    selectedPaymentMethod = PaymentMethod.Builder()
                        .setId(method.id)
                        .setType(PaymentMethodType.CARD)
                        .setCard(card)
                        .build()

                    notifyDataSetChanged()
                }
                selectedPaymentMethod?.let {
                    callback?.onPaymentMethodClick(it)
                }
            }
            itemView.ivCardChecked.visibility =
                if (selectedPaymentMethod?.type == PaymentMethodType.CARD && method.id == selectedPaymentMethod?.id) View.VISIBLE else View.GONE
        }
    }

    inner class WechatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.ivChecked.visibility =
                if (selectedPaymentMethod?.type == PaymentMethodType.WECHAT) View.VISIBLE else View.GONE

            itemView.rlWechatPay.setOnClickListener {
                if (selectedPaymentMethod?.type == PaymentMethodType.WECHAT) {
                    return@setOnClickListener
                }
                selectedPaymentMethod = PaymentMethod.Builder()
                    .setType(PaymentMethodType.WECHAT)
                    .setWechatPayFlow(WechatPayFlow(WechatPayFlowType.INAPP))
                    .build()

                notifyDataSetChanged()
                selectedPaymentMethod?.let {
                    callback?.onWechatClick(it)
                }
            }
        }
    }

    inner class AddCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.tvAddCard.setOnClickListener {
                callback?.onAddCardClick()
            }
        }
    }

    internal interface Callback {
        fun onPaymentMethodClick(paymentMethod: PaymentMethod)
        fun onWechatClick(paymentMethod: PaymentMethod)
        fun onAddCardClick()
    }

    internal enum class ItemViewType {
        Card,
        AddCard,
        Wechat
    }
}