package com.airwallex.android.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.R
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.payment_method_item_card.view.*
import kotlinx.android.synthetic.main.payment_method_item_wechat.view.*

internal class PaymentMethodsAdapter(
    val shouldShowCard: Boolean = false,
    val shouldShowWeChatPay: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val weChatCount = if (shouldShowWeChatPay) 1 else 0
    private var selectedPaymentMethod: PaymentMethod? = null
    private val paymentMethods = mutableListOf<PaymentMethod?>()
    internal var listener: Listener? = null

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
                    isLoading = true
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
        notifyDataSetChanged()
    }

    internal fun addNewPaymentMethod(paymentMethod: PaymentMethod) {
        selectedPaymentMethod = paymentMethod
        this.paymentMethods.add(0, paymentMethod)
        notifyDataSetChanged()
    }

    internal fun startLoadingMore(recyclerView: RecyclerView) {
        recyclerView.post {
            paymentMethods.add(null)
            notifyItemInserted(itemCount - 1)
        }
    }

    internal fun endLoadingMore() {
        paymentMethods.removeAt(paymentMethods.size - 1)
        notifyItemRemoved(itemCount)
        isLoading = false
    }

    override fun getItemCount(): Int {
        return weChatCount + if (shouldShowCard) paymentMethods.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isWeChatPosition(position) -> ItemViewType.WeChat.ordinal
            isLoadingPosition(position) -> ItemViewType.Loading.ordinal
            else -> ItemViewType.Card.ordinal
        }
    }

    private fun isWeChatPosition(position: Int): Boolean {
        return position == 0
    }

    private fun isLoadingPosition(position: Int): Boolean {
        return paymentMethods[position - weChatCount] == null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.WeChat -> WeChatHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_wechat, parent, false)
            )
            ItemViewType.Card -> CardHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_card, parent, false)
            )
            ItemViewType.Loading -> LoadingHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.payment_method_item_loading, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WeChatHolder -> holder.bindView()
            is CardHolder -> holder.bindView(position)
        }
    }

    inner class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("DefaultLocale")
        fun bindView(position: Int) {
            val method = paymentMethods[position - 1] ?: return
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
                        .setId(requireNotNull(method.id))
                        .setType(PaymentMethodType.CARD)
                        .setCard(card)
                        .build()

                    notifyDataSetChanged()
                }
                selectedPaymentMethod?.let {
                    listener?.onPaymentMethodClick(it)
                }
            }
            itemView.ivCardChecked.visibility =
                if (selectedPaymentMethod?.type == PaymentMethodType.CARD && method.id == selectedPaymentMethod?.id) View.VISIBLE else View.GONE
        }
    }

    inner class WeChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView() {
            itemView.ivChecked.visibility =
                if (selectedPaymentMethod?.type == PaymentMethodType.WECHAT) View.VISIBLE else View.GONE

            itemView.rlWeChatPay.setOnClickListener {
                if (selectedPaymentMethod?.type != PaymentMethodType.WECHAT) {
                    selectedPaymentMethod = PaymentMethod.Builder()
                        .setType(PaymentMethodType.WECHAT)
                        .setWeChatPayFlow(WeChatPayRequest(WeChatPayRequestFlow.IN_APP))
                        .build()
                    notifyDataSetChanged()
                }
                selectedPaymentMethod?.let {
                    listener?.onWeChatClick(it)
                }
            }
        }
    }

    inner class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal interface Listener {
        fun onPaymentMethodClick(paymentMethod: PaymentMethod)
        fun onWeChatClick(paymentMethod: PaymentMethod)
    }

    internal enum class ItemViewType {
        Card,
        WeChat,
        Loading
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
}
