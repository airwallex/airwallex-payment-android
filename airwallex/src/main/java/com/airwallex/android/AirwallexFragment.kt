package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.exception.ThreeDSException
import com.airwallex.android.model.AirwallexError
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse

internal class AirwallexFragment : Fragment() {

    internal var onActivityResultCompletion: ((validateResponse: ValidateResponse?, exception: AirwallexException?) -> Unit)? =
        null

    companion object {

        private const val AIRWALLEX_FRAGMENT_TAG = "AirwallexFragmentTag"

        fun newInstance(activity: FragmentActivity): AirwallexFragment {
            val manager = activity.supportFragmentManager
            var fragment: AirwallexFragment? =
                manager.findFragmentByTag(AIRWALLEX_FRAGMENT_TAG) as? AirwallexFragment
            if (fragment == null) {
                fragment = AirwallexFragment()
                manager
                    .beginTransaction()
                    .add(fragment, AIRWALLEX_FRAGMENT_TAG)
                    .commitAllowingStateLoss()
                manager.executePendingTransactions()
            }
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == ThreeDSecureActivity.THREE_D_SECURE &&
            data != null
        ) {
            try {
                ThreeDSecure.onActivityResult(data) { validateResponse, exception ->
                    onActivityResultCompletion?.invoke(validateResponse, exception)
                }
            } catch (e: Exception) {
                onActivityResultCompletion?.invoke(
                    null,
                    ThreeDSException(AirwallexError(message = e.localizedMessage))
                )
            }
        }
    }
}
