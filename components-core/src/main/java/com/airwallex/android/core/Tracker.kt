package com.airwallex.android.core

import com.airwallex.android.core.model.TrackerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Tracker {

    private val apiRepository = AirwallexApiRepository()

    fun track(request: TrackerRequest) {
        val options = AirwallexApiRepository.TrackerOptions(
            request = request
        )
        CoroutineScope(Dispatchers.IO).launch {
            apiRepository.tracker(options)
        }
    }
}
