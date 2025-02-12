package com.airwallex.paymentacceptance.ui

import CustomerDialog
import DemoCardDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.Environment
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.CardBrand
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.card
import com.airwallex.paymentacceptance.card3DS
import com.airwallex.paymentacceptance.databinding.ActivityApiIntegrationBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.ui.bean.ButtonItem
import com.airwallex.paymentacceptance.ui.widget.ButtonAdapter
import com.airwallex.paymentacceptance.viewmodel.APIIntegrationViewModel
import com.bumptech.glide.Glide
import java.util.Locale

/**
 * this Activity demonstrates how to call the low-level APIs provided by the Airwallex SDK.
 * you can flexibly organize your own UI based on these APIs.
 */
class APIIntegrationActivity :
    BasePaymentActivity<ActivityApiIntegrationBinding, APIIntegrationViewModel>() {
    private lateinit var adapter: ButtonAdapter

    override fun getViewBinding(): ActivityApiIntegrationBinding {
        return ActivityApiIntegrationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val recyclerView = mBinding.rvContent
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ButtonAdapter(emptyList()) { id -> handleBtnClick(id) }
        recyclerView.adapter = adapter
        refreshButtons(0)
        mBinding.dropdownView.setOptions(
            listOf(
                "One-off payment",
                "Recurring",
                "Recurring and payment"
            )
        )
        mBinding.dropdownView.setTitleText("Payment type")
    }

    override fun initListener() {
        mBinding.dropdownView.setOnOptionSelectedCallback { mode ->
            val selectedOption = when (mode) {
                "Recurring" -> 1
                "Recurring and payment" -> 2
                else -> 0
            }
            mViewModel.updateCheckoutModel(selectedOption)
            refreshButtons(selectedOption)
        }
        mBinding.flArrow.setOnClickListener {
            finish()
        }
        mBinding.imSetting.setOnClickListener {
            openSettingPage()
        }
        mBinding.titleView.setOnButtonClickListener {
            openSettingPage()
        }
    }

    override fun addObserver() {
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
        mViewModel.createPaymentIntentError.observe(this) { error ->
            setLoadingProgress(false)
            showAlert(
                getString(R.string.create_payment_intent_failed),
                error ?: getString(R.string.payment_failed_message)
            )
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

    private fun setBtnEnabled(btn: Button, isEnabled: Boolean) {
        btn.isEnabled = isEnabled
        val textColor = if (isEnabled) {
            ContextCompat.getColor(this, R.color.color_primary)
        } else {
            ContextCompat.getColor(this, R.color.airwallex_color_grey_30)
        }
        btn.setTextColor(textColor)
    }

    private fun openSettingPage() {
        startActivity(Intent(this, SettingActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun handleBtnClick(id: Int) {
        when (id) {
            1 -> {
                DemoCardDialog(this)
                    .setCardInfo(card)
                    .setPayCallBack { card ->
                        setLoadingProgress(true)
                        mViewModel.startPayWithCardDetail(card, saveCard = false)
                    }.show()
            }

            2 -> {
                DemoCardDialog(this)
                    .setCardInfo(card)
                    .setPayCallBack { card ->
                        setLoadingProgress(true)
                        mViewModel.startPayWithCardDetail(card, saveCard = true)
                    }.show()
            }

            3 -> {
                DemoCardDialog(this)
                    .setCardInfo(card3DS, false)
                    .setPayCallBack { card ->
                        setLoadingProgress(true)
                        mViewModel.startPayWithCardDetail(card, force3DS = true)
                    }.show()
            }

            4 -> {
                setLoadingProgress(true)
                mViewModel.startGooglePay()
            }

            5 -> {
                setLoadingProgress(true)
                mViewModel.startGooglePay(true)
            }

            6 -> {
                setLoadingProgress(true)
                mViewModel.startPayByRedirection()
            }

            7 -> {
                setLoadingProgress(true)
                mViewModel.getPaymentMethodsList()
            }

            8 -> {
                setLoadingProgress(true)
                mViewModel.getPaymentConsentList()
            }
        }
    }

    private fun refreshButtons(selectedOption: Int) {
        val fullButtonList = listOf(
            ButtonItem(1, "Pay with card"),
            ButtonItem(2, "Pay with card and save"),
            ButtonItem(3, "Pay with 3DS"), // Available only in non-production environments
            ButtonItem(4, "Pay with Google Pay"),
            ButtonItem(5, "Google Pay 3DS"),
            ButtonItem(6, "Redirect Payment"), // Only visible for "One-off payment"
            ButtonItem(7, "Get payment methods"),
            ButtonItem(8, "Get saved cards")
        )

        val filteredButtons = fullButtonList.filter { item ->
            when (item.id) {
                3 -> Settings.environment != Environment.PRODUCTION// Hide "3DS" in PRODUCTION
                6 -> selectedOption == 0 // Show "Redirect Payment" only for "One-off payment"
                2 -> selectedOption != 1 // Hide "Pay with card and save" in "Recurring" mode
                else -> true
            }
        }
        adapter.updateButtons(filteredButtons)
    }

    companion object {
        private const val TAG = "APIIntegrationActivity"
    }
}
