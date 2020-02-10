package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.model.WechatPayFlow
import com.airwallex.android.model.WechatPayFlowType
import com.airwallex.android.view.AddPaymentMethodActivity
import com.airwallex.paymentacceptance.PaymentBaseActivity.Companion.REQUEST_CONFIRM_CVC_CODE
import kotlinx.android.synthetic.main.payment_method_item_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_footer.view.*
import kotlinx.android.synthetic.main.payment_method_item_header.view.*

class PaymentMethodsAdapter(
    private val paymentMethods: List<PaymentMethod?>,
    private val context: Context,
    var paymentMethod: PaymentMethod?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemViewType(val value: Int) {
        HEADER(1),
        ITEM(2),
        FOOTER(3)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ItemViewType.HEADER.value
            }
            paymentMethods.size - 1 -> {
                ItemViewType.FOOTER.value
            }
            else -> {
                ItemViewType.ITEM.value
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemViewType.HEADER.value -> {
                HeaderHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.payment_method_item_header, parent, false)
                )
            }
            ItemViewType.FOOTER.value -> {
                FooterHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.payment_method_item_footer, parent, false)
                )
            }
            else -> {
                CardHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.payment_method_item_card, parent, false)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderHolder -> holder.bindView()
            is FooterHolder -> holder.bindView()
            is CardHolder -> holder.bindView(position)
        }
    }

    inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.ivChecked.visibility =
                if (paymentMethod?.type == PaymentMethodType.WECHAT) View.VISIBLE else View.GONE

            itemView.rlWechatPay.setOnClickListener {
                if (paymentMethod?.type == PaymentMethodType.WECHAT) {
                    return@setOnClickListener
                }
                paymentMethod = PaymentMethod.Builder()
                    .setType(PaymentMethodType.WECHAT)
                    .setWechatPayFlow(WechatPayFlow(WechatPayFlowType.INAPP))
                    .setBilling(PaymentData.billing)
                    .build()

                (context as PaymentMethodsActivity).invalidateOptionsMenu()
                notifyDataSetChanged()
            }
        }
    }

    inner class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(position: Int) {
            val method = paymentMethods[position] ?: return
            val card = method.card ?: return
            itemView.tvCardInfo.text =
                String.format("%s •••• %s", card.brand?.capitalize(), card.last4)
            when (card.brand) {
                "visa" -> itemView.ivCardIcon.setImageResource(R.drawable.airwallex_ic_visa)
                "mastercard" -> itemView.ivCardIcon.setImageResource(R.drawable.airwallex_ic_mastercard)
            }
            itemView.rlCard.setOnClickListener {
                val context = context as PaymentMethodsActivity
                if (paymentMethod?.type == PaymentMethodType.CARD && method.id == paymentMethod?.id) {
                    // No need to update payment method
                } else {
                    paymentMethod = PaymentMethod.Builder()
                        .setId(method.id)
                        .setType(PaymentMethodType.CARD)
                        .setCard(card)
                        .setBilling(PaymentData.billing)
                        .build()

                    context.invalidateOptionsMenu()
                    notifyDataSetChanged()
                }

                PaymentConfirmCvcActivity.startActivityForResult(
                    context,
                    paymentMethod,
                    context.paymentIntentId,
                    REQUEST_CONFIRM_CVC_CODE
                )
            }
            itemView.ivCardChecked.visibility =
                if (paymentMethod?.type == PaymentMethodType.CARD && method.id == paymentMethod?.id) View.VISIBLE else View.GONE
        }
    }

    inner class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.tvAddCard.setOnClickListener {
                AddPaymentMethodActivity.startActivityForResult(
                    context as Activity,
                    Store.token
                )
            }
        }
    }
}