package com.airwallex.android.view

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_airwallex.*

abstract class AirwallexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airwallex)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorResId())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    abstract fun onActionSave()

    @DrawableRes
    abstract fun homeAsUpIndicatorResId(): Int

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_save) {
            onActionSave()
            true
        } else {
            val handled = super.onOptionsItemSelected(item)
            if (!handled) {
                onBackPressed()
            }
            handled
        }
    }

    fun alert(message: String, completion: (() -> Unit)? = null) {
        showAlert(title = "", message = message, completion = completion)
    }

    fun alert(title: String, message: String, completion: (() -> Unit)? = null) {
        showAlert(title = title, message = message, completion = completion)
    }

    private fun showAlert(title: String, message: String, completion: (() -> Unit)? = null) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    completion?.invoke()
                }
                .create()
                .show()
        }
    }
}