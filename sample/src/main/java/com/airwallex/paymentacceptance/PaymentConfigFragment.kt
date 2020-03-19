package com.airwallex.paymentacceptance

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class PaymentConfigFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }
}