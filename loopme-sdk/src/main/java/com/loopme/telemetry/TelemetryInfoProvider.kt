package com.loopme.telemetry

import java.util.UUID

class TelemetryInfoProvider {
    fun getSessionId(): UUID = UUID.randomUUID()

    //or

//    fun getEventInfo(): EventInfo {}

}