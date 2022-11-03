package com.airwallex.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.core.Tracker
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.R
import com.airwallex.android.core.model.*
import com.airwallex.android.databinding.PaymentMethodConsentItemBinding
import com.airwallex.android.databinding.PaymentMethodDynamicItemBinding
import com.bumptech.glide.Glide
import java.util.*

internal class PaymentMethodsAdapter(
    val availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
    val listener: Listener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPaymentConsent: PaymentConsent? = null
    private val paymentConsents = mutableListOf<PaymentConsent>()

    internal fun isEmpty(): Boolean {
        return paymentConsents.isEmpty()
    }

    internal fun setPaymentConsents(paymentConsents: List<PaymentConsent>) {
        this.paymentConsents.addAll(paymentConsents)
        notifyItemRangeInserted(0, paymentConsents.size)
    }

    override fun getItemCount(): Int {
        return paymentConsents.size + availablePaymentMethodTypes.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isConsentPosition(position) -> ItemViewType.CONSENT.ordinal
            else -> ItemViewType.DYNAMIC.ordinal
        }
    }

    private fun isConsentPosition(position: Int): Boolean {
        return position < paymentConsents.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.CONSENT -> PaymentConsentHolder(parent.context, parent)
            ItemViewType.DYNAMIC -> DynamicPaymentHolder(parent.context, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DynamicPaymentHolder -> holder.bindView(position)
            is PaymentConsentHolder -> holder.bindView(position)
        }
    }

    @JvmSynthetic
    internal fun deletePaymentConsent(paymentConsent: PaymentConsent) {
        val index = paymentConsents.indexOf(paymentConsent)
        if (index >= 0) {
            paymentConsents.remove(paymentConsent)
            notifyItemRemoved(index)
        }
    }

    @JvmSynthetic
    internal fun resetPaymentConsent(paymentConsent: PaymentConsent) {
        val index = paymentConsents.indexOf(paymentConsent)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }

    @JvmSynthetic
    internal fun getPaymentConsentAtPosition(position: Int): PaymentConsent {
        return paymentConsents[position]
    }

    @JvmSynthetic
    internal fun getAvailablePaymentMethodTypeAtPosition(position: Int): AvailablePaymentMethodType {
        return availablePaymentMethodTypes[position - paymentConsents.size]
    }

    inner class PaymentConsentHolder(
        private val viewBinding: PaymentMethodConsentItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodConsentItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        @SuppressLint("NotifyDataSetChanged")
        fun bindView(position: Int) {
            val paymentConsent = getPaymentConsentAtPosition(position)
            val method = paymentConsent.paymentMethod ?: return
            val card = method.card ?: return
            viewBinding.tvCardInfo.text =
                String.format(
                    "%s •••• %s",
                    card.brand?.replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale.getDefault()
                            )
                        } else it.toString()
                    },
                    card.last4
                )

            val cardImage = card.brand?.let {
                CardBrand.fromType(it)
            }
            if (cardImage != null) {
                viewBinding.ivCardIcon.setImageResource(cardImage.icon)
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

    inner class DynamicPaymentHolder(
        private val viewBinding: PaymentMethodDynamicItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodDynamicItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        @SuppressLint("NotifyDataSetChanged")
        fun bindView(position: Int) {
            val paymentMethodType = getAvailablePaymentMethodTypeAtPosition(position)
            viewBinding.paymentMethodName.text =
                paymentMethodType.displayName ?: paymentMethodType.name
            viewBinding.paymentMethodChecked.visibility =
                if (selectedPaymentConsent?.paymentMethod?.type == paymentMethodType.name &&
                    paymentMethodType.name != PaymentMethodType.CARD.value
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            Glide.with(viewBinding.root.context)
                .load(paymentMethodType.resources?.logos?.png)
                .error(if (paymentMethodType.name == PaymentMethodType.CARD.value) R.drawable.airwallex_ic_card_default else 0)
                .into(viewBinding.paymentMethodIcon)
            itemView.setOnSingleClickListener {
                if (paymentMethodType.name == PaymentMethodType.CARD.value) {
                    val supportedCardSchemes = paymentMethodType.cardSchemes
                    if (supportedCardSchemes != null) {
                        listener?.onAddCardClick(supportedCardSchemes)
                    }
                } else {
                    selectedPaymentConsent = PaymentConsent(
                        paymentMethod = PaymentMethod.Builder()
                            .setType(paymentMethodType.name)
                            .build()
                    )
                    notifyDataSetChanged()

                    selectedPaymentConsent?.let {
                        listener?.onPaymentConsentClick(
                            it,
                            paymentMethodType
                        )
                    }
                }
            }
        }
    }

    internal interface Listener {
        fun onPaymentConsentClick(
            paymentConsent: PaymentConsent,
            paymentMethodType: AvailablePaymentMethodType? = null
        )

        fun onAddCardClick(supportedCardSchemes: List<CardScheme>)
    }

    internal enum class ItemViewType {
        CONSENT,
        DYNAMIC
    }
}
