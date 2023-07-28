import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.panteleyev.jpackage.ImageType
import org.panteleyev.jpackage.JPackageTask

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("org.panteleyev.jpackageplugin") version "1.5.2"
}

group = "me.dzikimlecz"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("com.github.gwenn:sqlite-dialect:0.1.4")
    implementation("org.hibernate:hibernate-core:6.2.7.Final")
    implementation("org.hibernate:hibernate-annotations:3.5.6-Final")

    runtimeOnly("org.slf4j:slf4j-api:2.0.5")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.5")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("me.dzikimlecz.coffeepot.MainKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "me.dzikimlecz.coffeepot.MainKt"
    }
}

tasks.register<Jar>("uberJar") {
    archiveClassifier.set("uber")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<JPackageTask> {
    type = ImageType.APP_IMAGE
    appName = "coffeepot"
    appVersion = version.toString()
    input = "build/libs/uber"
    mainJar = "coffeepot-$appVersion-uber.jar"
}