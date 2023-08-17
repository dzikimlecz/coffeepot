package me.dzikimlecz.coffeepot

object Resources {
    val trackedStops: List<String>
    val location: String
    val weatherImagePath: String
    val transitFeedUrl: String
    val appDirectory: String


    init {
        // TODO Replace hardcoding with resources file
        location = "Poznań"
        weatherImagePath = "w.png"
        transitFeedUrl = "https://ztm.poznan.pl/pl/dla-deweloperow/getGTFSFile"
        appDirectory = System.getProperty("user.home") + "/.coffeepot"
        trackedStops = listOf("Jasna Rola")
    }



}