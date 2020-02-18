package com.airwallex.paymentacceptance.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.AddPaymentBillingActivityStarter
import com.airwallex.paymentacceptance.R
import kotlinx.android.synthetic.main.billing_item.view.*
import java.util.*

class BillingItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private var billing: PaymentMethod.Billing? = null

    val isValid: Boolean
        get() {
            return billing == null
                    || billing != null
                    && !billing?.firstName.isNullOrBlank()
                    && !billing?.lastName.isNullOrBlank()
                    && !billing?.address?.countryCode.isNullOrBlank()
                    && !billing?.address?.state.isNullOrBlank()
                    && !billing?.address?.city.isNullOrBlank()
                    && !billing?.address?.street.isNullOrBlank()
                    && !billing?.email.isNullOrBlank()
                    && Patterns.EMAIL_ADDRESS.matcher(billing?.email ?: "").matches()
        }

    init {
        View.inflate(getContext(), R.layout.billing_item, this)

        rlBilling.setOnClickListener {
            AddPaymentBillingActivityStarter(context as Activity)
                .startForResult(
                    AddPaymentBillingActivityStarter.BillingArgs.Builder()
                        .setBilling(billing)
                        .build()
                )
        }
    }

    fun renewalBilling(billing: PaymentMethod.Billing?) {
        this.billing = billing

        if (billing == null) {
            tvShippingAddress.text =
                context.getString(R.string.same_as_shipping)
            tvShippingAddress.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.airwallex_color_dark_deep
                )
            )
            return
        }

        val countryName = billing.address?.countryCode?.let {
            val loc = Locale("", it)
            loc.displayCountry
        }

        tvShippingAddress.text = String.format(
            "%s %s\n%s\n%s, %s, %s",
            billing.lastName,
            billing.firstName,
            billing.address?.street,
            billing.address?.city,
            billing.address?.state,
            countryName
        )

        tvShippingAddress.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_dark_deep
            )
        )
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        completion: (billing: PaymentMethod.Billing?) -> Unit
    ) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            AddPaymentBillingActivityStarter.REQUEST_CODE -> {
                val result = AddPaymentBillingActivityStarter.Result.fromIntent(data)

                result?.let {
                    renewalBilling(result.billing)
                    completion(result.billing)
                }
            }
        }
    }
}