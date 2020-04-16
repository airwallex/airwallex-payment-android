package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device internal constructor(

    @SerializedName("device_id")
    val deviceId: String? = null

) : AirwallexModel, Parcelable {
    class Builder : ObjectBuilder<Device> {
        private var deviceId: String? = null

        fun setDeviceId(deviceId: String?): Builder = apply {
            this.deviceId = deviceId
        }

        override fun build(): Device {
            return Device(
                deviceId = deviceId
            )
        }
    }
}
