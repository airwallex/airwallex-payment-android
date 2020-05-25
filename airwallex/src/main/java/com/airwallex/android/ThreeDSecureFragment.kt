package com.airwallex.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.airwallex.android.model.AirwallexError

internal class ThreeDSecureFragment : Fragment() {

    /**
     * 3DS callback
     */
    internal var threeDSecureCallback: ThreeDSecureCallback? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == ThreeDSecureActivity.THREE_D_SECURE &&
            data != null
        ) {
            threeDSecureCallback?.let {
                try {
                    ThreeDSecure.onActivityResult(data, it)
                } catch (e: Exception) {
                    it.onFailed(AirwallexError(message = e.localizedMessage))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        threeDSecureCallback = null
        super.onDestroy()
    }

    companion object {
        private const val AIRWALLEX_FRAGMENT_TAG = "AirwallexFragmentTag"

        fun newInstance(manager: FragmentManager): ThreeDSecureFragment {
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
}
