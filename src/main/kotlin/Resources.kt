package me.dzikimlecz.coffeepot

import java.io.File
import java.util.Properties

object Resources {
    val location: String
    val locationCode: Pair<String, String>
    val transitFeedUrl: String
    val appDirectory: String
    val trackedStopCodes: List<String>
    val trackedStopNames: List<String>

    // TODO: remove
    val weatherImagePath: String = "w.png"
    val trackedStops: List<String>
        get() = trackedStopNames

    init {

        val configFile = File(
            System.getProperty("user.home"),
            ".config/coffeepot/config.properties"
        )

        val properties = readProperties(configFile)

        location = properties["location"].toString()
        transitFeedUrl = properties["transitFeedUrl"].toString()
        appDirectory = properties["appDirectory"].toString()
        trackedStopNames = properties["stopNames"]!!.toString()
            .split(",")
            .map(String::trim)
        trackedStopCodes = properties["stopCodes"]!!.toString()
            .split(",")
            .map(String::trim)

        locationCode = with(properties["locationCode"]!!.toString()) {
            if(isEmpty()) {
                "" to ""
            } else {
                val tokens = split(",")
                check(tokens.size == 2) {
                    "Malformed locationCode: $this. (Proper example: '1, 2')."
                }
                tokens[0] to tokens[1]
            }
        }

    }

}

private fun readProperties(configFile: File): Properties {
    require(configFile.exists()) { "File ${configFile.path} does not exist." }
    require(configFile.isFile) { "File ${configFile.path} is a directory." }
    require(configFile.canRead()) { "File ${configFile.path} is unreadable." }

    val defaults = Properties().apply {
        this["appDirectory"] =
            System.getProperty("user.home") + "/.coffeepot"
        this["locationCode"] = ""
        this["stopNames"] = ""
        this["stopCodes"] = ""
        this["transitFeedUrl"] = ""
    }

    val properties = Properties(defaults)
        .apply { load(configFile.inputStream()) }

    requireNotNull(properties["location"]) {
        "Location of weather services must be set."
    }

    val path = properties["appDirectory"].toString()
    with(File(path)) {
        require(exists()) {
            "Directory: $path does not exist."
        }
        require(isDirectory) {
            "$path is not a directory."
        }
    }

    return properties
}

