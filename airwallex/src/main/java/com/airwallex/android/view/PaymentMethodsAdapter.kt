package com.airwallex.android.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.R
import com.airwallex.android.Tracker
import com.airwallex.android.databinding.PaymentMethodItemCardBinding
import com.airwallex.android.databinding.PaymentMethodSpaceItemBinding
import com.airwallex.android.databinding.PaymentMethodThirdItemBinding
import com.airwallex.android.model.*
import java.util.*

internal class PaymentMethodsAdapter(
    val availableThirdPaymentTypes: List<PaymentMethodType>,
    val shouldShowCard: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPaymentConsent: PaymentConsent? = null
    private val paymentConsents = mutableListOf<PaymentConsent>()
    internal var listener: Listener? = null
    private val spaceCount = 1

    internal fun isEmpty(): Boolean {
        return paymentConsents.isEmpty()
    }

    internal fun setPaymentConsents(paymentConsents: List<PaymentConsent>) {
        this.paymentConsents.addAll(paymentConsents)
        notifyItemRangeInserted(itemCount, paymentConsents.size)
    }

    override fun getItemCount(): Int {
        return availableThirdPaymentTypes.size + // third part payment
            spaceCount + // space
            if (shouldShowCard) paymentConsents.size else 0 // card
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isThirdPosition(position) -> ItemViewType.ThirdPay.ordinal
            isSpacePosition(position) -> ItemViewType.Space.ordinal
            else -> ItemViewType.Card.ordinal
        }
    }

    private fun isThirdPosition(position: Int): Boolean {
        return position < availableThirdPaymentTypes.size
    }

    private fun isSpacePosition(position: Int): Boolean {
        return availableThirdPaymentTypes.size == position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.ThirdPay -> ThirdPayHolder(parent.context, parent)
            ItemViewType.Space -> SpaceHolder(parent.context, parent)
            ItemViewType.Card -> CardHolder(parent.context, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ThirdPayHolder -> holder.bindView(position)
            is SpaceHolder -> holder.bindView()
            is CardHolder -> holder.bindView(position)
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
            it + availableThirdPaymentTypes.size + spaceCount
        }
    }

    @JvmSynthetic
    internal fun getPaymentConsentAtPosition(position: Int): PaymentConsent {
        return paymentConsents[getPaymentConsentIndex(position)]
    }

    private fun getPaymentConsentIndex(position: Int): Int {
        return position - (availableThirdPaymentTypes.size + spaceCount)
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
            val paymentConsent = paymentConsents[position - availableThirdPaymentTypes.size - spaceCount]
            val method = paymentConsent.paymentMethod ?: return
            val card = method.card ?: return
            viewBinding.tvCardInfo.text =
                String.format("%s •••• %s", card.brand?.uppercase(Locale.ROOT), card.last4)
            when (card.brand) {
                CardBrand.Visa.type -> viewBinding.ivCardIcon.setImageResource(R.drawable.airwallex_ic_visa)
                CardBrand.MasterCard.type -> viewBinding.ivCardIcon.setImageResource(R.drawable.airwallex_ic_mastercard)
            }
            viewBinding.rlCard.setOnClickListener {
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
            viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.paymentMethod?.type == paymentMethodType) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
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

    inner class SpaceHolder(
        viewBinding: PaymentMethodSpaceItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodSpaceItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        fun bindView() {
            itemView.layoutParams.height = if (availableThirdPaymentTypes.isNotEmpty() && paymentConsents.size == 0) 0 else itemView.context.resources.getDimension(R.dimen.space_height).toInt()
        }
    }

    internal interface Listener {
        fun onPaymentConsentClick(paymentConsent: PaymentConsent)
    }

    internal enum class ItemViewType {
        Card,
        ThirdPay,
        Space
    }
}
