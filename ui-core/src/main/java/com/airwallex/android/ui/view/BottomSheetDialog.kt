package com.airwallex.android.ui.view

import android.os.Bundle
import android.view.WindowManager
import com.airwallex.android.ui.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BottomSheetDialog : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransBottomSheetDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }
}
