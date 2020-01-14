package com.airwallex.example

import android.app.Application
import com.airwallex.android.core.Configuration
import com.airwallex.android.core.Environment

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Configuration.init(this, "1", "1", Environment.STAGING)
    }
}