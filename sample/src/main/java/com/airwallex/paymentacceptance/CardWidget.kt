package com.airwallex.paymentacceptance

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget_card.view.*

class CardWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    TextWatcher {

    var cardChangeCallback: (() -> Unit)? = null

    val isValidCard: Boolean
        get() {
            return etCardNumber.text.isNotEmpty()
                    && etCardName.text.isNotEmpty()
                    && etExpires.text.isNotEmpty()
                    && etCVC.text.isNotEmpty()
        }

    init {
        View.inflate(getContext(), R.layout.widget_card, this)

        etCardNumber.addTextChangedListener(this)
        etCardName.addTextChangedListener(this)
        etExpires.addTextChangedListener(this)
        etCVC.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        cardChangeCallback?.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}