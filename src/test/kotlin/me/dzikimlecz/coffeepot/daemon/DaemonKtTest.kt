package me.dzikimlecz.coffeepot.daemon



import javafx.application.Platform
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

class DaemonKtTest {

    @BeforeEach
    fun setUp() {
        Platform.startup {  }
    }

    @Test
    fun `does fetchWeatherImage downloads a file`() {
        //given
        val loc = "Poznań"
        val path = "w.png"
        //when
        fetchWeatherImage(loc, path)
        //then
        assertTrue("File with an image should exist") {
            File(path).exists()
        }
    }
}