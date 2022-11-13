import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.moru3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.moruch4nn:MinePie:d5a75463ed")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn("shadowJar")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}