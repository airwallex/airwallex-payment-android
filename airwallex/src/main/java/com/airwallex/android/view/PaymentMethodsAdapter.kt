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
    val availableThirdPaymentTypes: List<AvaliablePaymentMethodType>,
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
                String.format("%s •••• %s", card.brand?.toUpperCase(Locale.ROOT), card.last4)
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
            when (availableThirdPaymentTypes[position]) {
                AvaliablePaymentMethodType.ALIPAY_CN -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_alipay_cn)
                    viewBinding.paymentMethodName.setText(R.string.alipay)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.ALIPAY_CN.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.ALIPAY_CN.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.ALIPAY_CN)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                AvaliablePaymentMethodType.WECHAT -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_wechat)
                    viewBinding.paymentMethodName.setText(R.string.wechat_pay)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.WECHAT.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.WECHAT.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.WECHAT)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                AvaliablePaymentMethodType.ALIPAY_HK -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_alipay_hk)
                    viewBinding.paymentMethodName.setText(R.string.alipay_hk)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.ALIPAY_HK.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.ALIPAY_HK.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.ALIPAY_HK)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                AvaliablePaymentMethodType.DANA -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_dana)
                    viewBinding.paymentMethodName.setText(R.string.dana)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.DANA.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.DANA.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.DANA)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                AvaliablePaymentMethodType.GCASH -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_gcash)
                    viewBinding.paymentMethodName.setText(R.string.gcash)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.GCASH.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.GCASH.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.GCASH)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                AvaliablePaymentMethodType.KAKAO -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_kakao_pay)
                    viewBinding.paymentMethodName.setText(R.string.kakao_pay)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.KAKAOPAY.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.KAKAOPAY.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.KAKAOPAY)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                AvaliablePaymentMethodType.TNG -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_touchngo)
                    viewBinding.paymentMethodName.setText(R.string.touchngo)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentConsent?.id == PaymentMethodType.TNG.value) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentConsent = PaymentConsent(
                            id = PaymentMethodType.TNG.value,
                            paymentMethod = PaymentMethod.Builder()
                                .setType(PaymentMethodType.TNG)
                                .build()
                        )
                        notifyDataSetChanged()

                        selectedPaymentConsent?.let { listener?.onPaymentConsentClick(it) }
                    }
                }
                else -> {
                    // Ignore
                }
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
