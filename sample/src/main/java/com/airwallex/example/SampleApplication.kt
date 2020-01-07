package com.airwallex.example

import android.app.Application
import com.airwallex.android.core.Configuration

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Configuration.init(this, "1", "1")
    }
}