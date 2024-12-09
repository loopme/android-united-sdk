package com.loopme.telemetry

import com.loopme.network.HttpUtils
import com.loopme.network.HttpUtilsWrapper
import com.loopme.utils.ExecutorHelperWrapper

class TelemetryEventSender(private val executorHelper: ExecutorHelperWrapper, private val httpUtils: HttpUtilsWrapper) {

    fun send(event: TelemetryEvent) {
        executorHelper.getExecutor().submit{
            httpUtils.track("telemetry url", event.toString())
        }
    }
}