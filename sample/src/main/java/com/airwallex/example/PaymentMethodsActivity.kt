package com.airwallex.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_payment_methods.*

class PaymentMethodsActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, PaymentMethodsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)

        val adapter = PaymentMethodsAdapter(
            listOf(PaymentCardFragment(), PaymentWeChatFragment()),
            supportFragmentManager,
            this
        )
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    class PaymentMethodsAdapter(
        private val fragments: List<Fragment>,
        fm: FragmentManager,
        val context: Context
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) {
                context.getString(R.string.payment_method_card)
            } else {
                context.getString(R.string.payment_method_we_chat)
            }
        }
    }
}