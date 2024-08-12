package com.airwallex.paymentacceptance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityMainBinding
import com.airwallex.paymentacceptance.h5.H5DemoActivity
import com.airwallex.paymentacceptance.ui.base.startActivity
import com.airwallex.paymentacceptance.wechat.WeChatDemoActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.btnAPI.setOnClickListener {
            startActivity(APIIntegrationActivity::class)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        viewBinding.btnUI.setOnClickListener {
            startActivity(UIIntegrationActivity::class)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        viewBinding.btnH5Demo.setOnClickListener {
            startActivity(H5DemoActivity::class)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        viewBinding.btnWeChatDemo.setOnClickListener {
            startActivity(WeChatDemoActivity::class)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}