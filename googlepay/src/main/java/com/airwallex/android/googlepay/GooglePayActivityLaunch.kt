package com.airwallex.android.googlepay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.ParcelableSession
import com.airwallex.android.core.Session
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.convertToSession
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.googlepay.GooglePayActivityLaunch.Args
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.ui.extension.getExtraResult
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

internal class GooglePayActivityLaunch :
    AirwallexActivityLaunch<GooglePayLauncherActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        GooglePayLauncherActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        GooglePayLauncherActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    internal data class Args(
        internal val parcelableSession: ParcelableSession? = null,
        @Suppress("DEPRECATION") internal val recurringSession: AirwallexRecurringSession? = null,
        val googlePayOptions: GooglePayOptions,
        val paymentMethodType: AvailablePaymentMethodType
    ) : AirwallexActivityLaunch.Args {

        val session: AirwallexSession
            get() = parcelableSession?.toSession()
                ?: recurringSession
                ?: error("No session provided in Args")

        companion object {
            @Suppress("DEPRECATION")
            fun create(
                session: AirwallexSession,
                googlePayOptions: GooglePayOptions,
                paymentMethodType: AvailablePaymentMethodType
            ): Args {
                val parcelableSession: ParcelableSession?
                val recurringSession: AirwallexRecurringSession?
                when (session) {
                    is AirwallexRecurringSession -> {
                        parcelableSession = null
                        recurringSession = session
                    }
                    is Session -> {
                        parcelableSession = ParcelableSession.from(session)
                        recurringSession = null
                    }
                    is AirwallexPaymentSession -> {
                        parcelableSession = ParcelableSession.from(session.convertToSession())
                        recurringSession = null
                    }
                    is AirwallexRecurringWithIntentSession -> {
                        parcelableSession = ParcelableSession.from(session.convertToSession())
                        recurringSession = null
                    }
                    else -> error("Unknown session type: ${session::class}")
                }
                return Args(
                    parcelableSession = parcelableSession,
                    recurringSession = recurringSession,
                    googlePayOptions = googlePayOptions,
                    paymentMethodType = paymentMethodType
                )
            }
        }
    }

    internal sealed class Result : AirwallexActivityLaunch.Result {
        @Parcelize
        data class Success(val info: @RawValue Map<String, Any>) : Result() {
            override fun toBundle(): Bundle {
                return Bundle().also {
                    it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
                }
            }
        }

        @Parcelize
        data class Failure(val exception: AirwallexException) : Result() {
            override fun toBundle(): Bundle {
                return Bundle().also {
                    it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
                }
            }
        }

        @Parcelize
        object Cancel : Result() {
            override fun toBundle(): Bundle {
                return Bundle().also {
                    it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
                }
            }
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getExtraResult()
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1007
    }
}