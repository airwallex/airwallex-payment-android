package com.airwallex.paymentacceptance

interface PayListener {
    fun onSuccess()

    fun onFailure(errCode: String?, errMessage: String?)

    fun onCancel()
}