package me.dzikimlecz.coffeepot.daemon



import me.dzikimlecz.coffeepot.fetchWeatherImage
import me.dzikimlecz.coffeepot.getWeather
import me.dzikimlecz.coffeepot.weatherProperty
import javafx.application.Platform
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Thread.sleep
import kotlin.test.assertTrue

class DaemonKtTest {



    @Test
    fun `does getWeather sets weatherProperty to a correct location`() {
        //given
        val loc = "Poznań"
        //when
        getWeather(loc)
        sleep(500)
        //then
        assertTrue("Weather should be fetched for the given location") {
            weatherProperty.get().contains(loc)
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            Platform.startup { }
        }
    }
}