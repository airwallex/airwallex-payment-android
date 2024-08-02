package com.airwallex.paymentacceptance.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.R
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
            APIIntegrationActivity.startActivity(this@MainActivity)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        viewBinding.btnUI.setOnClickListener {
            UIIntegrationActivity.startActivity(this@MainActivity)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        viewBinding.btnH5Demo.setOnClickListener {
            startActivity(Intent(this, H5DemoActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        viewBinding.btnWeChatDemo.setOnClickListener {
            startActivity(Intent(this, WeChatDemoActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as Activity).finish()
        }
    }
}