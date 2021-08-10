package com.airwallex.android.ui.view

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

class KeyboardController(
    private val activity: Activity
) {
    private val inputMethodManager: InputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    /**
     * Hide the keyboard
     */
    fun hide() {
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0
            )
        }
    }
}
