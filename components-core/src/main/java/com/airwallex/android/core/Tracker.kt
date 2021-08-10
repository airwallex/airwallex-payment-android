package com.airwallex.android.core

import com.airwallex.android.core.model.TrackerRequest

object Tracker {

    private val apiRepository = AirwallexApiRepository()

    fun track(request: TrackerRequest) {
        val options = AirwallexApiRepository.TrackerOptions(
            request = request
        )
        apiRepository.tracker(options)
    }
}
