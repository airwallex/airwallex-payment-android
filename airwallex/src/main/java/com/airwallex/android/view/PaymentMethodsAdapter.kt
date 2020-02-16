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
import com.airwallex.android.view.AddPaymentCardActivity
import com.airwallex.android.view.CardBrand
import kotlinx.android.synthetic.main.payment_method_item_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_footer.view.*
import java.util.*

class PaymentMethodsAdapter(
    private val paymentMethods: List<PaymentMethod?>,
    private val context: Context,
    var selectedPaymentMethod: PaymentMethod?,
    val paymentIntent: PaymentIntent,
    val token: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemViewType(val value: Int) {
        CARD(1),
        FOOTER(2)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == paymentMethods.size) {
            ItemViewType.FOOTER.value
        } else {
            ItemViewType.CARD.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ItemViewType.FOOTER.value) {
            FooterHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_footer, parent, false)
            )
        } else {
            CardHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_card, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return paymentMethods.size + 1
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FooterHolder -> holder.bindView()
            is CardHolder -> holder.bindView(position)
        }
    }

    inner class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("DefaultLocale")
        @ExperimentalStdlibApi
        fun bindView(position: Int) {
            val method = paymentMethods[position] ?: return
            val card = method.card ?: return
            itemView.tvCardInfo.text =
                String.format("%s •••• %s", card.brand?.capitalize(Locale.ENGLISH), card.last4)
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

    inner class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.tvAddCard.setOnClickListener {
                AddPaymentCardActivity.startActivityForResult(
                    context as Activity,
                    token,
                    paymentIntent.clientSecret
                )
            }

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
            }
        }
    }
}