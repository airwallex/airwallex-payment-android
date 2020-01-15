package com.airwallex.example

import android.app.Application
import com.airwallex.android.PaymentConfiguration
import com.airwallex.android.Environment

class SampleApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        PaymentConfiguration.init(
            this,
            "9092eb393908b656c2ed8134535b574c30e7a243718a1c08a06b8ea9278919f4550af02cac520e062518028204c1dc54",
            "DW19XFSMSUq4YPc7xkM4Nw",
            Environment.STAGING
        )
    }
}