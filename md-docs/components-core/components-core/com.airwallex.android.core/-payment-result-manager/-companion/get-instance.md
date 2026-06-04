//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[PaymentResultManager](../index.md)/[Companion](index.md)/[getInstance](get-instance.md)

# getInstance

[androidJvm]\
fun [getInstance](get-instance.md)(listener: [Airwallex.PaymentResultListener](../../-airwallex/-payment-result-listener/index.md) = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    // no op
                }
            }): [PaymentResultManager](../index.md)
