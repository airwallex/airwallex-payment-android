package com.airwallex.paymentacceptance

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class PaymentSettingsActivity : AppCompatActivity() {

    private val paymentSettingsFragment: PaymentSettingsFragment by lazy {
        PaymentSettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle(R.string.settings)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, paymentSettingsFragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.reset -> {
                paymentSettingsFragment.reset()
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        SampleApplication.instance.configAirwallex()
        super.onDestroy()
    }
}
