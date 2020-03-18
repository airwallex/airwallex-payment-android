package com.airwallex.android.view

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Logger
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_airwallex.*

/**
 * Base Airwallex Activity, provide same toolbar, save button...
 */
internal abstract class AirwallexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airwallex)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicatorResId())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewStub.layoutResource = layoutResource
        viewStub.inflate()

        Logger.debug("$localClassName#onCreate()")
    }

    override fun onDestroy() {
        Logger.debug("$localClassName#onDestroy()")
        super.onDestroy()
    }

    protected abstract val layoutResource: Int

    protected abstract fun onActionSave()

    @DrawableRes
    protected abstract fun homeAsUpIndicatorResId(): Int

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.airwallex_menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Logger.debug("$localClassName#onCreateOptionsMenu()")
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

    internal fun alert(title: String = "", message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }
}
