package com.loopme.telemetry

import java.util.UUID

data class TelemetryEvent(
    val sessionId: UUID,
    val type: EventType
)
