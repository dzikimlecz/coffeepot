import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "me.dzikimlecz"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("no.tornado:tornadofx:1.7.20")
    val oneBusAwayVersion = "1.3.4"
    implementation("org.onebusaway:onebusaway-gtfs:$oneBusAwayVersion")
    implementation("org.onebusaway:onebusaway-gtfs-hibernate:$oneBusAwayVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}

javafx {
    version = "20"
    modules = listOf("javafx.controls")
}