package com.loopme.telemetry

import com.loopme.network.HttpUtilsWrapper
import com.loopme.utils.ExecutorHelperWrapper

object TelemetryContainer {

    private val telemetryEventSender: TelemetryEventSender by lazy {
        println("creating new instance of lazy telemetryEventSender")
        TelemetryEventSender(ExecutorHelperWrapper(), HttpUtilsWrapper())
    }

    //if only one instance for app lifecycle -> to use singleton patter
    fun telemetryEventSenderSingleton(): TelemetryEventSender {
        println("getting new one instance of telemetryEventSender")
        return telemetryEventSender
    }

    //new instance everyTime
    fun telemetryEventSender(): TelemetryEventSender {
        println("getting new instance of telemetryEventSender and creating it")
        return TelemetryEventSender(ExecutorHelperWrapper(), HttpUtilsWrapper())
    }

    fun telemetryEventBuilder(): TelemetryEventBuilder {
        return TelemetryEventBuilder(TelemetryInfoProvider())
    }

}