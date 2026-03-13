package com.airwallex.android.view

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * DialogFragment-based loading dialog that handles configuration changes properly.
 * Unlike ProgressDialog, this survives rotation and prevents window attachment crashes.
 */
internal class AirwallexLoadingDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ProgressDialog(requireContext()).apply {
            setMessage("Loading...")
            setCancelable(false)
        }
    }

    companion object {
        private const val TAG = "AirwallexLoadingDialog"

        /**
         * Show loading dialog. Automatically handles:
         * - Configuration changes (rotation)
         * - Activity lifecycle
         * - Dismissing existing dialogs
         */
        fun show(context: Context) {
            val fragmentManager = context.findFragmentManager() ?: return

            // Dismiss existing dialog if any
            (fragmentManager.findFragmentByTag(TAG) as? DialogFragment)?.dismissAllowingStateLoss()

            // Show new dialog
            try {
                AirwallexLoadingDialogFragment().show(fragmentManager, TAG)
            } catch (e: IllegalStateException) {
                // Fragment transaction after state saved - ignore
            }
        }

        /**
         * Hide loading dialog safely
         */
        fun hide(context: Context) {
            val fragmentManager = context.findFragmentManager() ?: return
            (fragmentManager.findFragmentByTag(TAG) as? DialogFragment)?.dismissAllowingStateLoss()
        }

        private fun Context.findFragmentManager(): FragmentManager? {
            var currentContext: Context? = this
            while (currentContext is ContextWrapper) {
                if (currentContext is FragmentActivity) {
                    if (currentContext.isFinishing || currentContext.isDestroyed) {
                        return null
                    }
                    return currentContext.supportFragmentManager
                }
                currentContext = currentContext.baseContext
            }
            return null
        }
    }
}
