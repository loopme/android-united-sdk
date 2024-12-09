package com.loopme.network

import com.loopme.telemetry.TelemetryEvent

class HttpUtilsWrapper {

    fun track(url: String, body: String) {
        HttpUtils.track(url, body)
    }
}