package com.airwallex.android.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.core.Tracker
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.TrackerRequest
import com.airwallex.android.databinding.PaymentMethodItemCardBinding
import com.airwallex.android.databinding.PaymentMethodThirdItemBinding
import com.airwallex.android.R
import com.airwallex.android.databinding.PaymentMethodItemAddCardBinding
import com.airwallex.android.dto.drawableRes
import java.util.*

internal class PaymentMethodsAdapter(
    val availableThirdPaymentTypes: List<PaymentMethodType>,
    val shouldShowCard: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPaymentConsent: PaymentConsent? = null
    private val paymentConsents = mutableListOf<PaymentConsent>()
    internal var listener: Listener? = null

    internal fun isEmpty(): Boolean {
        return paymentConsents.isEmpty()
    }

    internal fun setPaymentConsents(paymentConsents: List<PaymentConsent>) {
        this.paymentConsents.addAll(paymentConsents)
        notifyItemRangeInserted(itemCount, paymentConsents.size)
    }

    override fun getItemCount(): Int {
        return availableThirdPaymentTypes.size + // third part payment
            (if (shouldShowCard) paymentConsents.size + 1 else 0) // card
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isThirdPosition(position) -> ItemViewType.ThirdPay.ordinal
            isCardPosition(position) -> ItemViewType.Card.ordinal
            else -> ItemViewType.AddCard.ordinal
        }
    }

    private fun isThirdPosition(position: Int): Boolean {
        return position < availableThirdPaymentTypes.size
    }

    private fun isCardPosition(position: Int): Boolean {
        return position >= availableThirdPaymentTypes.size && position < itemCount - 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.ThirdPay -> ThirdPayHolder(parent.context, parent)
            ItemViewType.Card -> CardHolder(parent.context, parent)
            ItemViewType.AddCard -> AddCardHolder(parent.context, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ThirdPayHolder -> holder.bindView(position)
            is CardHolder -> holder.bindView(position)
            is AddCardHolder -> holder.bindView()
        }
    }

    @JvmSynthetic
    internal fun deletePaymentConsent(paymentConsent: PaymentConsent) {
        getPosition(paymentConsent)?.let {
            paymentConsents.remove(paymentConsent)
            notifyItemRemoved(it)
        }
    }

    @JvmSynthetic
    internal fun resetPaymentMethod(paymentConsent: PaymentConsent) {
        getPosition(paymentConsent)?.let {
            notifyItemChanged(it)
        }
    }

    private fun getPosition(paymentConsent: PaymentConsent): Int? {
        return paymentConsents.indexOfFirst { it.id == paymentConsent.id }.takeIf { it >= 0 }?.let {
            it + availableThirdPaymentTypes.size
        }
    }

    @JvmSynthetic
    internal fun getPaymentConsentAtPosition(position: Int): PaymentConsent {
        return paymentConsents[getPaymentConsentIndex(position)]
    }

    private fun getPaymentConsentIndex(position: Int): Int {
        return position - availableThirdPaymentTypes.size
    }

    inner class CardHolder(
        private val viewBinding: PaymentMethodItemCardBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodItemCardBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        fun bindView(position: Int) {
            val paymentConsent =
                paymentConsents[position - availableThirdPaymentTypes.size]
            val method = paymentConsent.paymentMethod ?: return
            val card = method.card ?: return
            viewBinding.tvCardInfo.text =
                String.format(
                    "%s •••• %s",
                    card.brand?.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    },
                    card.last4
                )
            when (card.brand) {
                CardBrand.Visa.type -> viewBinding.ivCardIcon.setImageResource(R.drawable.airwallex_ic_visa)
                CardBrand.MasterCard.type -> viewBinding.ivCardIcon.setImageResource(R.drawable.airwallex_ic_mastercard)
            }
            viewBinding.rlCard.setOnSingleClickListener {
                if (selectedPaymentConsent?.id != paymentConsent.id) {
                    selectedPaymentConsent = paymentConsent

                    Tracker.track(
                        TrackerRequest.Builder()
                            .setBrand(card.brand)
                            .setCardBin(card.bin)
                            .setCode(TrackerRequest.TrackerCode.ON_SWITCH_METHOD)
                            .build()
                    )

                    notifyDataSetChanged()
                }
                selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
            }
            viewBinding.ivCardChecked.visibility =
                if (selectedPaymentConsent?.id == paymentConsent.id) View.VISIBLE else View.GONE
        }
    }

    inner class ThirdPayHolder(
        private val viewBinding: PaymentMethodThirdItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodThirdItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        fun bindView(position: Int) {
            val paymentMethodType = availableThirdPaymentTypes[position]
            viewBinding.paymentMethodIcon.setImageResource(paymentMethodType.drawableRes)
            viewBinding.paymentMethodName.text = paymentMethodType.displayName
            viewBinding.paymentMethodChecked.visibility =
                if (selectedPaymentConsent?.paymentMethod?.type == paymentMethodType) View.VISIBLE else View.GONE
            itemView.setOnSingleClickListener {
                selectedPaymentConsent = PaymentConsent(
                    paymentMethod = PaymentMethod.Builder()
                        .setType(paymentMethodType)
                        .build()
                )
                notifyDataSetChanged()

                selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
            }
        }
    }

    inner class AddCardHolder(
        val viewBinding: PaymentMethodItemAddCardBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodItemAddCardBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        fun bindView() {
            if (paymentConsents.size > 0) {
                viewBinding.tvAddCardInfo.setText(R.string.airwallex_pay_with_another_card)
            } else {
                viewBinding.tvAddCardInfo.setText(R.string.airwallex_pay_with_card)
            }

            itemView.setOnClickListener {
                listener?.onAddCardClick()
            }
        }
    }

    internal interface Listener {
        fun onPaymentConsentClick(paymentConsent: PaymentConsent)

        fun onAddCardClick()
    }

    internal enum class ItemViewType {
        Card,
        AddCard,
        ThirdPay
    }
}
