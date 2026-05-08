package com.airwallex.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewStub
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.airwallex.android.core.AirwallexInternalActivity
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.ui.databinding.ActivityAirwallexBinding

abstract class AirwallexActivity : AppCompatActivity(), AirwallexInternalActivity {

    private val viewBinding: ActivityAirwallexBinding by lazy {
        ActivityAirwallexBinding.inflate(layoutInflater)
    }

    val viewStub: ViewStub by lazy {
        viewBinding.viewStub
    }

    private val toolbar: Toolbar by lazy {
        viewBinding.toolbar
    }

    val loading: Boolean
        get() {
            return supportFragmentManager.findFragmentByTag(AirwallexLoadingDialogFragment.TAG) != null
        }

    private var isLoadingBeforeConfigChange = false
    private var loadingCancelable = true

    companion object {
        private const val KEY_IS_LOADING = "airwallex_is_loading"
        private const val KEY_LOADING_CANCELABLE = "airwallex_loading_cancelable"
        private const val KEY_LAUNCH_BUNDLE = "airwallex_launch_bundle"
        internal const val EVENT_INIT_FAILED = "activity_init_failed"
    }

    /**
     * Restore the launch bundle from savedInstanceState back into the intent so
     * args lazy reads succeed after process death. Idempotent — only restores if
     * the intent doesn't already carry the bundle.
     */
    protected fun restoreLaunchBundleIfNeeded(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        if (intent.hasExtra(AirwallexActivityLaunch.Args.AIRWALLEX_BUNDLE_EXTRA)) return
        savedInstanceState.getBundle(KEY_LAUNCH_BUNDLE)?.let { launchBundle ->
            intent.putExtra(AirwallexActivityLaunch.Args.AIRWALLEX_BUNDLE_EXTRA, launchBundle)
        }
    }

    abstract fun onBackButtonPressed()

    open fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorResId())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        AirwallexLogger.debug("$localClassName#onCreate()")
        if (this is TrackablePage) {
            AnalyticsLogger.logPageView(pageName, additionalInfo)
        }
    }

    open fun addListener() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackButtonPressed()
                }
            }
        )
    }

    open fun addObserver() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        restoreLaunchBundleIfNeeded(savedInstanceState)
        super.onCreate(savedInstanceState)
        try {
            setContentView(viewBinding.root)
            initView()
            addListener()
            addObserver()

            // Restore loading dialog state after configuration change
            savedInstanceState?.let {
                isLoadingBeforeConfigChange = it.getBoolean(KEY_IS_LOADING, false)
                loadingCancelable = it.getBoolean(KEY_LOADING_CANCELABLE, true)
                if (isLoadingBeforeConfigChange) {
                    setLoadingProgress(loading = true, cancelable = loadingCancelable)
                }
            }
        } catch (e: IllegalArgumentException) {
            // Required launch args were null — typically because the launch
            // Intent's nested parcelable failed to re-marshal after process
            // death. Fail the activity instead of crashing the host app so the
            // merchant gets RESULT_CANCELED and can recover.
            AirwallexLogger.error("$localClassName: init failed, finishing", e)
            AnalyticsLogger.logError(
                EVENT_INIT_FAILED,
                mapOf("activity" to localClassName),
            )
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save loading dialog state before configuration change
        outState.putBoolean(KEY_IS_LOADING, loading)
        outState.putBoolean(KEY_LOADING_CANCELABLE, loadingCancelable)
        // Backup the launch bundle so process-death recreation can recover args
        // even if the framework loses the inner parcelable.
        intent.getBundleExtra(AirwallexActivityLaunch.Args.AIRWALLEX_BUNDLE_EXTRA)?.let {
            outState.putBundle(KEY_LAUNCH_BUNDLE, it)
        }
    }

    override fun onDestroy() {
        AirwallexLogger.debug("$localClassName#onDestroy()")
        // Only dismiss if this is final destruction, not configuration change
        if (!isChangingConfigurations) {
            AirwallexLoadingDialogFragment.hide(this)
        }
        super.onDestroy()
    }

    @DrawableRes
    protected abstract fun homeAsUpIndicatorResId(): Int

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        AirwallexLogger.debug("$localClassName#onCreateOptionsMenu()")
        val handled = super.onOptionsItemSelected(item)
        if (!handled) {
            onBackPressedDispatcher.onBackPressed()
        }
        return handled
    }

    fun alert(title: String = "", message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.airwallex_okay) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }

    open fun setLoadingProgress(loading: Boolean, cancelable: Boolean = true) {
        loadingCancelable = cancelable
        if (loading) {
            AirwallexLoadingDialogFragment.show(this)
        } else {
            AirwallexLoadingDialogFragment.hide(this)
        }
    }
}
