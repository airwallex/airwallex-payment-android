package com.airwallex.android.ui

import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.ui.composables.AirwallexThemeConfig

/**
 * DialogFragment-based loading dialog that handles configuration changes properly.
 * Shows a transparent loading indicator with dimmed background.
 */
class AirwallexLoadingDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AirwallexLoadingDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.airwallex_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar = view.findViewById<ProgressBar>(R.id.airwallex_progress_bar)
        progressBar?.indeterminateTintList = ColorStateList.valueOf(
            AirwallexThemeConfig.themeColor.toArgb()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    companion object {
        const val TAG = "AirwallexLoadingDialog"

        /**
         * Show loading dialog. Automatically handles:
         * - Configuration changes (rotation)
         * - Activity lifecycle
         * - Reuses existing dialog if already showing (no duplicate)
         */
        fun show(context: Context) {
            val fragmentManager = context.findFragmentManager() ?: return

            // Check if already showing
            val existingDialog = fragmentManager.findFragmentByTag(TAG) as? AirwallexLoadingDialogFragment
            if (existingDialog != null && existingDialog.isAdded) {
                return
            }

            // Show new dialog
            try {
                val newDialog = AirwallexLoadingDialogFragment()
                newDialog.show(fragmentManager, TAG)
            } catch (_: IllegalStateException) {
                // Fragment transaction after state saved - ignore
                AirwallexLogger.debug("AirwallexLoadingDialog: Cannot show dialog - fragment transaction after state saved")
            }
        }

        /**
         * Hide loading dialog safely
         */
        fun hide(context: Context) {
            val fragmentManager = context.findFragmentManager() ?: return
            val existingDialog = fragmentManager.findFragmentByTag(TAG) as? DialogFragment
            if (existingDialog != null) {
                existingDialog.dismissAllowingStateLoss()
            }
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
