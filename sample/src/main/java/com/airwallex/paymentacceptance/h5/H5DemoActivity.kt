package com.airwallex.paymentacceptance.h5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityH5DemoBinding

class H5DemoActivity : AppCompatActivity() {

    private val viewBinding: ActivityH5DemoBinding by lazy {
        ActivityH5DemoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.h5demo)

        viewBinding.buttonFirst.setOnClickListener {
            val url = viewBinding.airwallexUrl.text.toString()
            if (url.isEmpty()) {
                Toast.makeText(this, "Url should not be empty!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val referer = viewBinding.refererUrl.text.toString()
            if (referer.isEmpty()) {
                Toast.makeText(this, "Referer should not be empty!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(this, H5WebViewActivity::class.java)
            intent.putExtra(H5WebViewActivity.URL, url)
            intent.putExtra(H5WebViewActivity.REFERER, referer)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
