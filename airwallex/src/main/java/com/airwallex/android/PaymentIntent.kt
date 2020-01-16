package com.airwallex.android

import android.os.Parcel

data class PaymentIntent(

    override val id: String?

) : AirwallexIntent, AirwallexModel {

}