package com.airwallex.android.googlepay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.exception.AirwallexException
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
        val session: AirwallexSession,
        val googlePayOptions: GooglePayOptions,
        val paymentMethodType: AvailablePaymentMethodType
    ) : AirwallexActivityLaunch.Args

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