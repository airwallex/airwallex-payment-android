package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.view.AddPaymentMethodActivityLaunch.Args
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.ui.AirwallexActivityLaunch
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

internal class AddPaymentMethodActivityLaunch :
    AirwallexActivityLaunch<AddPaymentMethodActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        AddPaymentMethodActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        AddPaymentMethodActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val session: AirwallexSession,
        val supportedCardSchemes: List<CardScheme>,
        val isSinglePaymentMethod: Boolean
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var session: AirwallexSession
            private lateinit var supportedCardSchemes: List<CardScheme>
            private var isSinglePaymentMethod: Boolean = false

            fun setAirwallexSession(session: AirwallexSession): Builder = apply {
                this.session = session
            }

            fun setSupportedCardSchemes(supportedCardSchemes: List<CardScheme>): Builder = apply {
                this.supportedCardSchemes = supportedCardSchemes
            }

            fun setSinglePaymentMethod(isSinglePaymentMethod: Boolean): Builder = apply {
                this.isSinglePaymentMethod = isSinglePaymentMethod
            }

            override fun build(): Args {
                return Args(
                    session = session,
                    supportedCardSchemes = supportedCardSchemes,
                    isSinglePaymentMethod = isSinglePaymentMethod
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requireNotNull(intent.getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA, Args::class.java))
                } else {
                    requireNotNull(intent.getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA))
                }
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentIntentId: String? = null,
        val exception: AirwallexException? = null
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object : Parceler<Result> {

            override fun create(parcel: Parcel): Result {
                return Result(
                    paymentIntentId = parcel.readString(),
                    exception = parcel.readSerializable() as? AirwallexException?
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeString(paymentIntentId)
                parcel.writeSerializable(exception)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    @Parcelize
    internal data class CancellationResult internal constructor(
        val isSinglePaymentMethod: Boolean
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object {

            fun fromIntent(intent: Intent?): CancellationResult? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }
}
