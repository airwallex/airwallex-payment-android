package com.airwallex.android

import com.airwallex.android.model.TrackerRequest

internal object Tracker {

    private val apiRepository = AirwallexApiRepository()

    fun track(request: TrackerRequest) {
        val options = AirwallexApiRepository.TrackerOptions(
            request = request
        )
        apiRepository.tracker(options)
    }
}
