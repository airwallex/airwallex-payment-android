package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_add_card.editShippingLayout
import kotlinx.android.synthetic.main.activity_add_card.toolbar

class EditCardActivity : AppCompatActivity() {

    private var menu: Menu? = null

    companion object {
        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, EditCardActivity::class.java),
                requestCode
            )
        }
    }

    private fun updateMenuStatus() {
        menu?.findItem(R.id.menu_save)?.isEnabled =
            etCardNumber.text.isNotEmpty()
                    && etCardName.text.isNotEmpty()
                    && etExpires.text.isNotEmpty()
                    && etCVC.text.isNotEmpty()
                    && editShippingLayout.isValidShipping()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        editShippingLayout.onShippingChanged = {
            updateMenuStatus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> {
                actionSave()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(IllegalArgumentException::class)
    private fun actionSave() {

    }

}