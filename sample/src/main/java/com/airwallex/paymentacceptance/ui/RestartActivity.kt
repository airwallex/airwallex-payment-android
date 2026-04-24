package com.airwallex.paymentacceptance.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Trampoline activity running in a separate process (":restart").
 * Survives main process death and relaunches MainActivity to trigger a fresh start.
 */
class RestartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
        finish()
    }
}