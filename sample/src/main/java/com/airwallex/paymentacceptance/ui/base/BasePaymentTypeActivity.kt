package com.airwallex.paymentacceptance.ui.base

import android.content.Intent
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.databinding.ActivityPaymentTypeBinding
import com.airwallex.paymentacceptance.ui.SettingActivity
import com.airwallex.paymentacceptance.ui.bean.ButtonItem
import com.airwallex.paymentacceptance.ui.widget.ButtonAdapter
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.launch

abstract class BasePaymentTypeActivity<VM : BaseViewModel> :
    BasePaymentActivity<ActivityPaymentTypeBinding, VM>() {

    protected lateinit var adapter: ButtonAdapter

    override fun getViewBinding(): ActivityPaymentTypeBinding {
        return ActivityPaymentTypeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mBinding.rvContent.layoutManager = LinearLayoutManager(this)
        adapter = ButtonAdapter(getButtonList()) { id -> handleBtnClick(id) }
        mBinding.rvContent.adapter = adapter
        refreshButtons(0)

        mBinding.rvContent.post { adjustLineMargin() }
        updateCheckoutModel(0)
        setupDropdown()
    }

    override fun initListener() {
        mBinding.flArrow.setOnClickListener { finish() }
        mBinding.imSetting.setOnClickListener { openSettingPage() }
        mBinding.titleView.setOnButtonClickListener { openSettingPage() }
        mBinding.dropdownView.setOnOptionSelectedCallback { mode ->
            handleDropdownSelection(mode)
        }
    }

    override fun addObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mViewModel.createPaymentIntentError.collect { error ->
                    showAlert(
                        getString(R.string.create_payment_intent_failed),
                        error ?: getString(R.string.payment_failed_message)
                    )
                }
            }
        }
    }

    protected open fun setupDropdown() {
        mBinding.dropdownView.setOptions(
            listOf(
                "One-off payment",
                "Recurring",
                "Recurring and payment"
            )
        )
        mBinding.dropdownView.setTitleText("Payment type")
    }

    protected open fun handleDropdownSelection(mode: String) {
        val selectedOption = when (mode) {
            "Recurring" -> 1
            "Recurring and payment" -> 2
            else -> 0
        }
        updateCheckoutModel(selectedOption)
        refreshButtons(selectedOption)
    }

    protected fun adjustLineMargin(
        preferredSpacing: Int = 180.dpToPx()
    ) {
        val itemCount = adapter.itemCount
        val bottomSpacing: Int = 20.dpToPx()
        val minMargin: Int = 20.dpToPx()
        val itemHeight = 80.dpToPx()
        val totalContentHeight = itemCount * itemHeight

        val screenHeight = resources.displayMetrics.heightPixels

        val topFixedHeight = mBinding.flArrow.measuredHeight +
                mBinding.titleContainer.measuredHeight +
                mBinding.dropdownView.measuredHeight +
                44.dpToPx()

        val maxAvailableHeight = screenHeight - topFixedHeight

        val newMarginTop = when {
            totalContentHeight < maxAvailableHeight - preferredSpacing -> {
                // Use preferred spacing when there's plenty of space
                maxAvailableHeight - totalContentHeight - bottomSpacing
            }
            totalContentHeight >= maxAvailableHeight - minMargin -> minMargin
            else -> maxAvailableHeight - totalContentHeight
        }.let { calculatedMargin ->
            // Ensure we have a valid range for coerceIn
            val maxMargin = maxAvailableHeight - totalContentHeight - minMargin
            if (maxMargin >= minMargin) {
                calculatedMargin.coerceIn(minMargin, maxMargin)
            } else {
                // When screen is too small, just use minimum margin
                minMargin
            }
        }

        val params = mBinding.line.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = newMarginTop
        mBinding.line.layoutParams = params
    }

    protected open fun openSettingPage() {
        startActivity(Intent(this, SettingActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    protected abstract fun getButtonList(): List<ButtonItem>

    protected abstract fun handleBtnClick(id: Int)

    protected abstract fun refreshButtons(selectedOption: Int)

    private fun updateCheckoutModel(mode: Int) {
        val checkoutMode = when (mode) {
            1 -> AirwallexCheckoutMode.RECURRING
            2 -> AirwallexCheckoutMode.RECURRING_WITH_INTENT
            else -> AirwallexCheckoutMode.PAYMENT
        }
        Settings.checkoutMode = checkoutMode
    }

    protected fun setBtnEnabled(btn: Button, isEnabled: Boolean) {
        btn.isEnabled = isEnabled
        val textColor = if (isEnabled) {
            ContextCompat.getColor(this, R.color.color_primary)
        } else {
            ContextCompat.getColor(this, R.color.airwallex_color_grey_30)
        }
        btn.setTextColor(textColor)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}