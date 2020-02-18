package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.AddPaymentCardActivityStarter.CardArgs
import kotlinx.android.parcel.Parcelize

class AddPaymentCardActivityStarter constructor(
    activity: Activity
) : ActivityStarter<AddPaymentCardActivity, CardArgs>(
    activity,
    AddPaymentCardActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class CardArgs internal constructor(
        internal val token: String,
        internal val clientSecret: String
    ) : Args {

        class Builder(
            private val token: String,
            private val clientSecret: String
        ) :
            ObjectBuilder<CardArgs> {

            override fun build(): CardArgs {
                return CardArgs(
                    token = token,
                    clientSecret = clientSecret
                )
            }
        }

        internal companion object {
            internal fun create(intent: Intent): CardArgs {
                return requireNotNull(intent.getParcelableExtra(Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    data class Result internal constructor(
        val paymentMethod: PaymentMethod,
        val cvc: String
    ) : ActivityStarter.Result {
        override fun toBundle(): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(ActivityStarter.Result.AIRWALLEX_EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(ActivityStarter.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }

}