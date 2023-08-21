import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    application
}

apply(from = "deploy.gradle")

group = "com.lee.consul"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api("com.orbitz.consul:consul-client:1.5.3")
    compileOnly("io.ktor:ktor-server-host-common:2.3.2")
    compileOnly("io.ktor:ktor-client:2.3.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}