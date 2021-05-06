package com.airwallex.android.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.R
import com.airwallex.android.Tracker
import com.airwallex.android.databinding.PaymentMethodItemCardBinding
import com.airwallex.android.databinding.PaymentMethodItemLoadingBinding
import com.airwallex.android.databinding.PaymentMethodSpaceItemBinding
import com.airwallex.android.databinding.PaymentMethodThirdItemBinding
import com.airwallex.android.model.*
import java.util.*

internal class PaymentMethodsAdapter(
    val availableThirdPaymentTypes: List<AvaliablePaymentMethodType>,
    val shouldShowCard: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPaymentMethod: PaymentMethod? = null
    private val paymentMethods = mutableListOf<PaymentMethod?>()
    internal var listener: Listener? = null
    private val spaceCount = 1

    private var lastVisibleItem = 0
    private var totalItemCount: Int = 0
    private var isLoading: Boolean = false
    internal var onLoadMoreCallback: () -> Unit = {}
    internal var hasMore = true

    internal fun addOnScrollListener(recyclerView: RecyclerView) {
        val viewManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = viewManager.itemCount
                lastVisibleItem = viewManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD && hasMore) {
                    onLoadMoreCallback.invoke()
                }
            }
        })
    }

    internal fun isEmpty(): Boolean {
        return paymentMethods.isEmpty()
    }

    internal fun setPaymentMethods(paymentMethods: List<PaymentMethod>, hasMore: Boolean) {
        this.hasMore = hasMore
        this.paymentMethods.addAll(paymentMethods)
        notifyItemRangeInserted(itemCount, paymentMethods.size)
    }

    internal fun addNewPaymentMethod(paymentMethod: PaymentMethod) {
        selectedPaymentMethod = paymentMethod
        this.paymentMethods.add(0, paymentMethod)
        notifyItemInserted(availableThirdPaymentTypes.size + spaceCount)
    }

    internal fun startLoadingMore() {
        isLoading = true
        paymentMethods.add(null)
        notifyItemInserted(itemCount)
    }

    internal fun endLoadingMore() {
        paymentMethods.removeAt(paymentMethods.size - 1)
        notifyItemRemoved(itemCount)
        isLoading = false
    }

    override fun getItemCount(): Int {
        return availableThirdPaymentTypes.size + // third part payment
            spaceCount + // space
            if (shouldShowCard) paymentMethods.size else 0 // card
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isThirdPosition(position) -> ItemViewType.ThirdPay.ordinal
            isSpacePosition(position) -> ItemViewType.Space.ordinal
            isLoadingPosition(position) -> ItemViewType.Loading.ordinal
            else -> ItemViewType.Card.ordinal
        }
    }

    private fun isThirdPosition(position: Int): Boolean {
        return position < availableThirdPaymentTypes.size
    }

    private fun isSpacePosition(position: Int): Boolean {
        return availableThirdPaymentTypes.size == position
    }

    private fun isLoadingPosition(position: Int): Boolean {
        return shouldShowCard && paymentMethods[position - availableThirdPaymentTypes.size - spaceCount] == null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.ThirdPay -> ThirdPayHolder(parent.context, parent)
            ItemViewType.Space -> SpaceHolder(parent.context, parent)
            ItemViewType.Loading -> LoadingHolder(parent.context, parent)
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
    internal fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        getPosition(paymentMethod)?.let {
            paymentMethods.remove(paymentMethod)
            notifyItemRemoved(it)
        }
    }

    @JvmSynthetic
    internal fun resetPaymentMethod(paymentMethod: PaymentMethod) {
        getPosition(paymentMethod)?.let {
            notifyItemChanged(it)
        }
    }

    private fun getPosition(paymentMethod: PaymentMethod): Int? {
        return paymentMethods.indexOfFirst { it?.id == paymentMethod.id }.takeIf { it >= 0 }?.let {
            it + availableThirdPaymentTypes.size + spaceCount
        }
    }

    @JvmSynthetic
    internal fun getPaymentMethodAtPosition(position: Int): PaymentMethod {
        return paymentMethods[getPaymentMethodIndex(position)]!!
    }

    private fun getPaymentMethodIndex(position: Int): Int {
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
            val method = paymentMethods[position - availableThirdPaymentTypes.size - spaceCount]
                ?: return
            val card = method.card ?: return
            viewBinding.tvCardInfo.text =
                String.format("%s •••• %s", card.brand?.toUpperCase(Locale.ROOT), card.last4)
            when (card.brand) {
                CardBrand.Visa.type -> viewBinding.ivCardIcon.setImageResource(R.drawable.airwallex_ic_visa)
                CardBrand.MasterCard.type -> viewBinding.ivCardIcon.setImageResource(R.drawable.airwallex_ic_mastercard)
            }
            viewBinding.rlCard.setOnClickListener {
                if (selectedPaymentMethod?.type != method.type || method.id != selectedPaymentMethod?.id) {
                    selectedPaymentMethod = PaymentMethod.Builder()
                        .setId(requireNotNull(method.id))
                        .setType(method.type!!)
                        .setCard(card)
                        .build()

                    Tracker.track(
                        TrackerRequest.Builder()
                            .setBrand(card.brand)
                            .setCardBin(card.bin)
                            .setCode(TrackerRequest.TrackerCode.ON_SWITCH_METHOD)
                            .build()
                    )

                    notifyDataSetChanged()
                }
                selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
            }
            viewBinding.ivCardChecked.visibility =
                if (selectedPaymentMethod?.type == method.type && method.id == selectedPaymentMethod?.id) View.VISIBLE else View.GONE
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
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.ALIPAY_CN) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.ALIPAY_CN)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                AvaliablePaymentMethodType.WECHAT -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_wechat)
                    viewBinding.paymentMethodName.setText(R.string.wechat_pay)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.WECHAT) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.WECHAT)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                AvaliablePaymentMethodType.ALIPAY_HK -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_alipay_hk)
                    viewBinding.paymentMethodName.setText(R.string.alipay_hk)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.ALIPAY_HK) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.ALIPAY_HK)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                AvaliablePaymentMethodType.DANA -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_dana)
                    viewBinding.paymentMethodName.setText(R.string.dana)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.DANA) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.DANA)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                AvaliablePaymentMethodType.GCASH -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_gcash)
                    viewBinding.paymentMethodName.setText(R.string.gcash)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.GCASH) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.GCASH)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                AvaliablePaymentMethodType.KAKAO -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_kakao_pay)
                    viewBinding.paymentMethodName.setText(R.string.kakao_pay)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.KAKAOPAY) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.KAKAOPAY)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                AvaliablePaymentMethodType.TNG -> {
                    viewBinding.paymentMethodIcon.setImageResource(R.drawable.airwallex_ic_touchngo)
                    viewBinding.paymentMethodName.setText(R.string.touchngo)
                    viewBinding.paymentMethodChecked.visibility = if (selectedPaymentMethod?.type == PaymentMethodType.TNG) View.VISIBLE else View.GONE
                    itemView.setOnClickListener {
                        selectedPaymentMethod = PaymentMethod.Builder()
                            .setType(PaymentMethodType.TNG)
                            .build()
                        notifyDataSetChanged()

                        selectedPaymentMethod?.let { listener?.onPaymentMethodClick(it) }
                    }
                }
                else -> {
                    // Ignore
                }
            }
        }
    }

    inner class LoadingHolder(
        viewBinding: PaymentMethodItemLoadingBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        constructor(context: Context, parent: ViewGroup) : this(
            PaymentMethodItemLoadingBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
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
            itemView.layoutParams.height = if (availableThirdPaymentTypes.isNotEmpty()) itemView.context.resources.getDimension(R.dimen.space_height).toInt() else 0
        }
    }

    internal interface Listener {
        fun onPaymentMethodClick(paymentMethod: PaymentMethod)
    }

    internal enum class ItemViewType {
        Card,
        ThirdPay,
        Loading,
        Space
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
}
