package me.dzikimlecz.coffeepot.transit

import java.time.LocalDateTime

data class Service(
    val line: String,
    val stop: String,
    val direction: String,
    val departure: LocalDateTime,
)
