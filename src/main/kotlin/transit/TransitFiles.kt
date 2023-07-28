package me.dzikimlecz.coffeepot.transit

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipInputStream

fun File.unzipTo(outputDir: File) {
    val stream = ZipInputStream(inputStream())
    var entry = stream.nextEntry
    while (entry != null) {
        val entryFile = File(outputDir, entry.name)
        if (entry.isDirectory) {
            entryFile.mkdirs()
        } else {
            entryFile.parentFile?.mkdirs()
            val outputStream = entryFile.outputStream()
            outputStream.write(stream.readBytes())
            outputStream.close()
        }

        stream.closeEntry()
        entry = stream.nextEntry
    }
    stream.close()
}

fun readData(inputDir: File): Gtfs =
    Gtfs(
        readTrips(File(inputDir, "trips.txt")),
        readRoutes(File(inputDir, "routes.txt")),
        readStops(File(inputDir, "stops.txt")),
        readStopTimes(File(inputDir, "stop_times.txt")),
        readCalendars(File(inputDir, "calendar.txt")),
    )

fun readTrips(inputFile: File) = readAndMap(inputFile) {
    Trip(
        routeId = it[0],
        calendarId = it[1],
        id = it[2],
        tripHeadsign = it[3],
        direction = if (it[4] == "0") Direction.TO else Direction.FROM,
        wheelchairAccessible = it[6] == "1",
    )
}

fun readRoutes(inputFile: File) = readAndMap(inputFile) {
    Route(
        id = it[0],
        shortName = it[2],
        longName = it[3],
        description = it[4],
        vehicle = if (it[5] == "0") Vehicle.TRAM else Vehicle.BUS,
        color = it[6],
        textColor = it[7],
    )
}

fun readStops(inputFile: File) = readAndMap(inputFile) {
    Stop(
        id = it[0],
        code = it[1],
        name = it[2],
        latitude = it[3].toDouble(),
        longitude = it[4].toDouble(),
        zone = it[5]
    )
}

fun readStopTimes(inputFile: File) = readAndMap(inputFile) {
    StopTime(
        tripId = it[0],
        arrivalTime = LocalTime.parse(it[1], DateTimeFormatter.ofPattern("HH:mm:ss")),
        departureTime = LocalTime.parse(it[2], DateTimeFormatter.ofPattern("HH:mm:ss")),
        stopId = it[3],
        stopSequence = it[4],
        stopHeadsign = it[5],
        pickupType = when(it[6]) {
            "0" -> StopHandlingMode.POSSIBLE
            "1" -> StopHandlingMode.IMPOSSIBLE
            else -> StopHandlingMode.ON_DEMAND
        },
        dropOffType = when(it[6]) {
            "0" -> StopHandlingMode.POSSIBLE
            "1" -> StopHandlingMode.IMPOSSIBLE
            else -> StopHandlingMode.ON_DEMAND
        },
    )
}
fun readCalendars(inputFile: File) = readAndMap(inputFile) {
    Calendar(
        id = it[0],
        monday = it[1] == "1",
        tuesday = it[2] == "1",
        wednesday = it[3] == "1",
        thursday = it[4] == "1",
        friday = it[5] == "1",
        saturday = it[6] == "1",
        sunday = it[7] == "1",
        startDate = LocalDate.parse(it[8], DateTimeFormatter.ofPattern("yyyMMdd")),
        endDate = LocalDate.parse(it[9], DateTimeFormatter.ofPattern("yyyMMdd")),
    )
}

private fun<T> readAndMap(inputFile: File, mapper: (CSVRecord) -> T): List<T> =
    CSVFormat.Builder.create().apply {
        setIgnoreSurroundingSpaces(true)
    }.build().parse(inputFile.reader())
        .drop(1)
        .map(mapper)