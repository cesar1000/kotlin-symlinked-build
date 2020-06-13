import java.nio.file.Files
import java.nio.file.Paths

plugins {
    kotlin("jvm") version "1.4-M2"
}

repositories {
	jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

val otherBuildDir = file("build2")
if (!otherBuildDir.exists()) {
    otherBuildDir.mkdirs()
    Files.createSymbolicLink(Paths.get(buildDir.absolutePath), Paths.get(otherBuildDir.absolutePath))
}

dependencies {
    testImplementation("junit:junit:4.12")
    testImplementation(kotlin("stdlib"))
}
