package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.AddPaymentCardActivityStarter.Args
import kotlinx.android.parcel.Parcelize

class AddPaymentCardActivityStarter constructor(
    activity: Activity
) : ActivityStarter<AddPaymentCardActivity, Args>(
    activity,
    AddPaymentCardActivity::class.java,
    Args.DEFAULT,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        internal val token: String?,
        internal val clientSecret: String?
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private var token: String? = null
            private var clientSecret: String? = null

            fun setToken(token: String): Builder = apply {
                this.token = token
            }

            fun setClientSecret(clientSecret: String): Builder = apply {
                this.clientSecret = clientSecret
            }

            override fun build(): Args {
                return Args(
                    token = token,
                    clientSecret = clientSecret
                )
            }
        }

        internal companion object {
            internal val DEFAULT = Builder().build()

            @JvmSynthetic
            internal fun create(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(ActivityStarter.Args.EXTRA))
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
            bundle.putParcelable(ActivityStarter.Result.EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(ActivityStarter.Result.EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1001
    }

}