package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.databinding.ActivityMainBinding
import com.airwallex.paymentacceptance.h5.H5DemoActivity
import com.airwallex.paymentacceptance.wechat.WeChatDemoActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.btnAPI.setOnClickListener {
            PaymentAPIIntegrationActivity.startActivity(this@MainActivity)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        viewBinding.btnUI.setOnClickListener {
            PaymentUIIntegrationActivity.startActivity(this@MainActivity)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, PaymentSettingsActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                true
            }
            R.id.h5demo -> {
                startActivity(Intent(this, H5DemoActivity::class.java))
                true
            }
            R.id.weChat_demo -> {
                startActivity(Intent(this, WeChatDemoActivity::class.java))
                true
            }
            else -> false
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as Activity).finish()
        }
    }
}