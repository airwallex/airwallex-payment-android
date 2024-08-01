package com.airwallex.paymentacceptance.ui

import CustomerDialog
import DemoCardDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.Environment
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.CardBrand
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityApiIntegrationBinding
import com.airwallex.paymentacceptance.ui.base.BaseMvvmActivity
import com.airwallex.paymentacceptance.viewmodel.APIIntegrationViewModel
import com.bumptech.glide.Glide
import java.util.Locale

/**
 * this Activity demonstrates how to call the low-level APIs provided by the Airwallex SDK.
 * you can flexibly organize your own UI based on these APIs.
 */
class APIIntegrationActivity :
    BaseMvvmActivity<ActivityApiIntegrationBinding, APIIntegrationViewModel>() {

    override fun getViewBinding(): ActivityApiIntegrationBinding {
        return ActivityApiIntegrationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mBinding.radioGroup.check(R.id.radioPayment)
        mBinding.radioGroup.check(R.id.radioPayment)
        setBtnEnabled(
            mBinding.btnPayWithCardDetail3DS,
            mViewModel.environment != Environment.PRODUCTION
        )
    }

    override fun initListener() {
        mBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.radioRecurring -> 1
                R.id.radioRecurringAndPayment -> 2
                else -> 0
            }
            mViewModel.updateCheckoutModel(selectedOption)
            setBtnEnabled(mBinding.btnGooglePay, selectedOption == 0)
        }

        mBinding.btnPayWithCardDetail.setOnClickListener {
            DemoCardDialog(this)
                .setCardInfo(mViewModel.card)
                .setPayCallBack {
                    setLoadingProgress(true)
                    mViewModel.startPayWithCardDetail(it, saveCard = false)
                }.show()
        }
        mBinding.btnPayWithCardDetailSaveCard.setOnClickListener {
            DemoCardDialog(this)
                .setCardInfo(mViewModel.card)
                .setPayCallBack {
                    setLoadingProgress(true)
                    mViewModel.startPayWithCardDetail(it, saveCard = true)
                }.show()
        }
        mBinding.btnPayWithCardDetail3DS.setOnClickListener {
            DemoCardDialog(this)
                .setCardInfo(mViewModel.card3DS, false)
                .setPayCallBack {
                    setLoadingProgress(true)
                    mViewModel.startPayWithCardDetail(it, force3DS = true)
                }.show()
        }
        mBinding.btnGooglePay.setOnClickListener {
            setLoadingProgress(true)
            mViewModel.startGooglePay()
        }
        mBinding.btnPaymentMethodsList.setOnClickListener {
            setLoadingProgress(true)
            mViewModel.getPaymentMethodsList()
        }
        mBinding.btnPaymentConsentsList.setOnClickListener {
            setLoadingProgress(true)
            mViewModel.getPaymentConsentList()
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

    companion object {
        private const val TAG = "APIIntegrationActivity"
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, APIIntegrationActivity::class.java))
        }
    }
}
