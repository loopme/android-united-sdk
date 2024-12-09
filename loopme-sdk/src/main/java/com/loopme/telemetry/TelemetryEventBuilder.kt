package com.loopme.telemetry

class TelemetryEventBuilder(private val infoProvider: TelemetryInfoProvider) {

    fun build(type: EventType): TelemetryEvent {
        // many methods here
        val sessionId = infoProvider.getSessionId()

        //or just one method: infoProvider.getEventInfo

        return TelemetryEvent(sessionId = sessionId, type = type)
    }

}