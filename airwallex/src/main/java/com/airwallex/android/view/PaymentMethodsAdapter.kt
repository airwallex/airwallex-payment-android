package com.airwallex.android.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.R
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.payment_method_item_add_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_wechat.view.*

class PaymentMethodsAdapter(
    private val paymentMethods: List<PaymentMethod>,
    private val context: Context,
    var selectedPaymentMethod: PaymentMethod?,
    val paymentIntent: PaymentIntent,
    val token: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return paymentMethods.size + ADD_PAYMENT_CARD_COUNT + WECHAT_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isWechatPosition(position) -> ItemViewType.Wechat.ordinal
            isPaymentMethodsPosition(position) -> ItemViewType.Card.ordinal
            else -> ItemViewType.AddCard.ordinal
        }
    }

    private fun isWechatPosition(position: Int): Boolean {
        return position == itemCount - 1
    }

    private fun isPaymentMethodsPosition(position: Int): Boolean {
        val range = paymentMethods.indices
        return position in range
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
                val context = context as PaymentMethodsActivity
                if (selectedPaymentMethod?.type != PaymentMethodType.CARD || method.id != selectedPaymentMethod?.id) {
                    selectedPaymentMethod = PaymentMethod.Builder()
                        .setId(method.id)
                        .setType(PaymentMethodType.CARD)
                        .setCard(card)
                        .build()

                    context.invalidateOptionsMenu()
                    notifyDataSetChanged()
                }
                selectedPaymentMethod?.let {
                    context.onSavePaymentMethod(paymentMethod = it)
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

                (context as PaymentMethodsActivity).invalidateOptionsMenu()
                notifyDataSetChanged()
                selectedPaymentMethod?.let {
                    context.onSavePaymentMethod(paymentMethod = it)
                }
            }
        }
    }

    inner class AddCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.tvAddCard.setOnClickListener {
                AddPaymentCardActivityStarter(context as Activity)
                    .startForResult(
                        AddPaymentCardActivityStarter.Args.Builder()
                            .setToken(token)
                            .setClientSecret(paymentIntent.clientSecret)
                            .build()
                    )
            }
        }
    }

    internal companion object {
        private const val ADD_PAYMENT_CARD_COUNT = 1
        private const val WECHAT_COUNT = 1
    }

    internal enum class ItemViewType {
        Card,
        AddCard,
        Wechat
    }
}