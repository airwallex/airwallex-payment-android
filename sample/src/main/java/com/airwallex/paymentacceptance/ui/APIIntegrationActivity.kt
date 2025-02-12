package com.airwallex.paymentacceptance.ui

import CustomerDialog
import DemoCardDialog
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.Environment
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.card
import com.airwallex.paymentacceptance.card3DS
import com.airwallex.paymentacceptance.ui.base.BasePaymentTypeActivity
import com.airwallex.paymentacceptance.ui.bean.ButtonItem
import com.airwallex.paymentacceptance.viewmodel.APIIntegrationViewModel
import com.bumptech.glide.Glide
import java.util.Locale

/**
 * this Activity demonstrates how to call the low-level APIs provided by the Airwallex SDK.
 * you can flexibly organize your own UI based on these APIs.
 */
class APIIntegrationActivity : BasePaymentTypeActivity<APIIntegrationViewModel>() {

    override fun initView() {
        super.initView()
        mBinding.titleView.setTitle("Integrate with low-level API")
    }

    override fun getViewModelClass(): Class<APIIntegrationViewModel> {
        return APIIntegrationViewModel::class.java
    }

    override fun getButtonList(): List<ButtonItem> {
        return emptyList()
    }

    override fun refreshButtons(selectedOption: Int) {
        val fullButtonList = listOf(
            ButtonItem(PAY_WITH_CARD, "Pay with card"),
            ButtonItem(PAY_WITH_CARD_AND_SAVE, "Pay with card and save card"),
            ButtonItem(PAY_WITH_3DS, "Pay with card and trigger 3DS"), // Available only in non-production environments
            ButtonItem(PAY_WITH_GOOGLE_PAY, "Pay with Google Pay"),
            ButtonItem(GOOGLE_PAY_3DS, "Google Pay 3DS"),
            ButtonItem(REDIRECT_PAYMENT, "Redirect Payment"), // Only visible for "One-off payment"
            ButtonItem(GET_PAYMENT_METHODS, "Get payment methods"),
            ButtonItem(GET_SAVED_CARDS, "Get saved cards")
        )

        val filteredButtons = fullButtonList.filter { item ->
            when (item.id) {
                PAY_WITH_3DS -> Settings.environment != Environment.PRODUCTION// Hide "3DS" in PRODUCTION
                REDIRECT_PAYMENT -> selectedOption == 0 // Show "Redirect Payment" only for "One-off payment"
                PAY_WITH_CARD_AND_SAVE -> selectedOption != 1 // Hide "Pay with card and save" in "Recurring" mode
                else -> true
            }
        }
        adapter.updateButtons(filteredButtons)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                adjustLineMargin()
            }
        })
        mBinding.rvContent.post { adjustLineMargin() }
    }

    override fun handleBtnClick(id: Int) {
        when (id) {
            PAY_WITH_CARD -> {
                DemoCardDialog(this)
                    .setCardInfo(card)
                    .setPayCallBack { card ->
                        setLoadingProgress(true)
                        mViewModel.startPayWithCardDetail(card, saveCard = false)
                    }.show()
            }

            PAY_WITH_CARD_AND_SAVE -> {
                DemoCardDialog(this)
                    .setCardInfo(card)
                    .setPayCallBack { card ->
                        setLoadingProgress(true)
                        mViewModel.startPayWithCardDetail(card, saveCard = true)
                    }.show()
            }

            PAY_WITH_3DS -> {
                DemoCardDialog(this)
                    .setCardInfo(card3DS, false)
                    .setPayCallBack { card ->
                        setLoadingProgress(true)
                        mViewModel.startPayWithCardDetail(card, force3DS = true)
                    }.show()
            }

            PAY_WITH_GOOGLE_PAY -> {
                setLoadingProgress(true)
                mViewModel.startGooglePay()
            }

            GOOGLE_PAY_3DS -> {
                setLoadingProgress(true)
                mViewModel.startGooglePay(true)
            }

            REDIRECT_PAYMENT -> {
                setLoadingProgress(true)
                mViewModel.startPayByRedirection()
            }

            GET_PAYMENT_METHODS -> {
                setLoadingProgress(true)
                mViewModel.getPaymentMethodsList()
            }

            GET_SAVED_CARDS -> {
                setLoadingProgress(true)
                mViewModel.getPaymentConsentList()
            }
        }
    }

    override fun addObserver() {
        super.addObserver()
        mViewModel.airwallexPaymentStatus.observe(this) { status ->
            handleStatusUpdate(status)
        }
        mViewModel.paymentMethodList.observe(this) { list ->
            setLoadingProgress(false)
            showPaymentMethodList(list)

        }
        mViewModel.paymentConsentList.observe(this) { list ->
            setLoadingProgress(false)
            showPaymentConsentList(list)
        }
    }

    var customerDialog: CustomerDialog<PaymentConsent>? = null
    private fun showPaymentConsentList(list: List<PaymentConsent>) {
        customerDialog =
            CustomerDialog(this, list, object : CustomerDialog.Binder<PaymentConsent> {
                override fun bind(
                    holder: CustomerDialog.CustomerAdapter.CustomerViewHolder,
                    item: PaymentConsent
                ) {
                    val method = item.paymentMethod
                    method?.card?.apply {
                        holder.itemText.text = String.format(
                            "%s •••• %s",
                            brand?.replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase(
                                        Locale.getDefault()
                                    )
                                } else it.toString()
                            },
                            last4
                        )
                        this.brand?.let {
                            CardBrand.fromName(it)
                        }?.let {
                            holder.itemImage.setImageResource(it.icon)
                        }
                        holder.btnPay.setOnClickListener {
                            mViewModel.startPayWithConsent(item)
                            setLoadingProgress(true)
                            customerDialog?.dismiss()
                        }
                        setBtnEnabled(
                            holder.btnPay,
                            mBinding.dropdownView.currentOption == "One-off payment"
                        )
                    }
                }
            })
        customerDialog?.show()
    }

    private fun showPaymentMethodList(list: List<AvailablePaymentMethodType>) {
        val customerDialog =
            CustomerDialog(this, list, object : CustomerDialog.Binder<AvailablePaymentMethodType> {
                override fun bind(
                    holder: CustomerDialog.CustomerAdapter.CustomerViewHolder,
                    item: AvailablePaymentMethodType
                ) {
                    Glide.with(this@APIIntegrationActivity)
                        .load(item.resources?.logos?.png)
                        .error(if (item.name == PaymentMethodType.CARD.value) com.airwallex.android.R.drawable.airwallex_ic_card_default else 0)
                        .fitCenter()
                        .into(holder.itemImage)
                    holder.itemText.text = item.displayName ?: item.name
                    holder.btnPay.visibility = View.GONE
                }
            })
        customerDialog.show()
    }

    private fun handleStatusUpdate(status: AirwallexPaymentStatus) {
        setLoadingProgress(false)
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                Log.d(TAG, "Payment success ${status.paymentIntentId}")
                showPaymentSuccess()
            }

            is AirwallexPaymentStatus.InProgress -> {
                // redirecting
                Log.d(TAG, "Payment is redirecting ${status.paymentIntentId}")
                showPaymentInProgress()
            }

            is AirwallexPaymentStatus.Failure -> {
                showPaymentError(status.exception.localizedMessage)
            }

            is AirwallexPaymentStatus.Cancel -> {
                Log.d(TAG, "User cancel the payment")
                showPaymentCancelled()
            }
        }
    }

    companion object {
        private const val TAG = "APIIntegrationActivity"
        const val PAY_WITH_CARD = 1
        const val PAY_WITH_CARD_AND_SAVE = 2
        const val PAY_WITH_3DS = 3
        const val PAY_WITH_GOOGLE_PAY = 4
        const val GOOGLE_PAY_3DS = 5
        const val REDIRECT_PAYMENT = 6
        const val GET_PAYMENT_METHODS = 7
        const val GET_SAVED_CARDS = 8
    }
}
