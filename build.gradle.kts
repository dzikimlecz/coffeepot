import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    application
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

    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("com.github.gwenn:sqlite-dialect:0.1.4")

    runtimeOnly("org.slf4j:slf4j-api:2.0.5")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.5")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    implementation(project(":libgtfskt-core"))
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
    dependsOn(tasks.build)
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


