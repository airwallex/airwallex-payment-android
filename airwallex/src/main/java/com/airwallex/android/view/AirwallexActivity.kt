package com.airwallex.android.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_airwallex.*

abstract class AirwallexActivity : AppCompatActivity() {

    companion object {
        const val TOKEN = "token"
    }

    protected abstract fun onActionSave()

    protected abstract fun menuEnable(): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airwallex)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.navigationIcon?.mutate()?.setColorFilter(
            resources.getColor(R.color.airwallex_blue),
            PorterDuff.Mode.SRC_IN
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_save)?.isEnabled = menuEnable()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_payment_method, menu)
        return true
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

    fun showError(message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }
}