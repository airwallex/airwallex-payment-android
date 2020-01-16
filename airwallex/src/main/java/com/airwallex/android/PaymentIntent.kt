package com.airwallex.android

import com.airwallex.android.model.AirwallexModel

data class PaymentIntent(

    override val id: String?

) : AirwallexIntent, AirwallexModel {

}