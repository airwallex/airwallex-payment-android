package com.airwallex.paymentacceptance

interface WechatPaymentListener {
    fun onSuccess()

    fun onFailure(errCode: String?, errMessage: String?)

    fun onCancel()
}