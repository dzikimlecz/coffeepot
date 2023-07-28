package me.dzikimlecz.coffeepot.transit

import java.time.LocalDateTime

data class UpcomingService(
    val line: String,
    val stop: String,
    val direction: String,
    val departure: LocalDateTime,
)
