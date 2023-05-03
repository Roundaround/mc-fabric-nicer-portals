plugins {
  id("roundalib") version "0.2.65"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  maven("https://maven.fabricmc.net/")
  maven("https://maven.rnda.dev/releases/")
}

dependencies {
  implementation("fabric-loom", "fabric-loom.gradle.plugin", "1.1-SNAPSHOT")
  implementation("roundalib", "roundalib.gradle.plugin", "0.2.65")
}
