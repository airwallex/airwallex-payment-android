package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airwallex.android.model.AirwallexError

internal class ThreeDSecureFragment : Fragment() {

    internal lateinit var threeDSecureCallback: ThreeDSecureCallback

    companion object {
        private const val AIRWALLEX_FRAGMENT_TAG = "AirwallexFragmentTag"

        fun newInstance(activity: FragmentActivity): ThreeDSecureFragment {
            val manager = activity.supportFragmentManager
            var fragment: ThreeDSecureFragment? =
                manager.findFragmentByTag(AIRWALLEX_FRAGMENT_TAG) as? ThreeDSecureFragment
            if (fragment == null) {
                fragment = ThreeDSecureFragment()
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
                ThreeDSecure.onActivityResult(data, threeDSecureCallback)
            } catch (e: Exception) {
                threeDSecureCallback.onFailed(AirwallexError(message = e.localizedMessage))
            }
        }
    }
}
