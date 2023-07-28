package me.dzikimlecz.coffeepot.transit

import org.hibernate.annotations.Entity
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

data class Gtfs(
    val trips: List<Trip>,
    val routes: List<Route>,
    val stops: List<Stop>,
    val stopTimes: List<StopTime>,
    val calendars: List<Calendar>,
)

@Entity @Table(name = "routes")
data class Route(
    @Id @Column(name = "route_id")
    val id: String,
    val shortName: String,
    val longName: String,
    val description: String,
    val vehicle: Vehicle,
    val color: String,
    val textColor: String,
)

@Entity @Table(name = "trips")
data class Trip (
    @Id @Column(name = "trip_id")
    val id: String,
    @ManyToOne @JoinColumn(name = "calendar_id")
    val calendarId: String,
    @ManyToOne @JoinColumn(name = "calendar_id")
    val routeId: String,
    val tripHeadsign: String,
    val direction: Direction,
    val wheelchairAccessible: Boolean,
)

@Entity @Table(name = "stops")
data class Stop(
    @Id @Column(name = "stop_id")
    val id: String,
    val code: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val zone: String,
)

@Entity @Table(name = "stoptimes")
data class StopTime(
    @ManyToOne @JoinColumn(name = "trip_id")
    val tripId: String,
    val arrivalTime: LocalTime,
    val departureTime: LocalTime,
    @ManyToOne @JoinColumn(name = "stop_id")
    val stopId: String,
    val stopSequence: String,
    val stopHeadsign: String,
    val pickupType: StopHandlingMode,
    val dropOffType: StopHandlingMode,
)

@Entity @Table(name = "calendars")
data class Calendar(
    @Id @Column(name = "calendar_id")
    val id: String,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    val weekdays: List<Weekday>
        @Transient get() {
            val list = mutableListOf<Weekday>()
            if (monday) {
                list += Weekday.MONDAY
            }
            if (tuesday) {
                list += Weekday.TUESDAY
            }
            if (wednesday) {
                list += Weekday.WEDNESDAY
            }
            if (thursday) {
                list += Weekday.THURSDAY
            }
            if (friday) {
                list += Weekday.FRIDAY
            }
            if (saturday) {
                list += Weekday.SATURDAY
            }
            if (sunday) {
                list += Weekday.SUNDAY
            }
            return list
        }
}


enum class Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
}

enum class Vehicle {
    TRAM,
    BUS,
}

enum class Direction {
    TO,
    FROM,
}

enum class StopHandlingMode {
    POSSIBLE,
    IMPOSSIBLE,
    ON_DEMAND,
}
