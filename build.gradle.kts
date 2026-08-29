plugins {
  id("fabric-loom") version "1.9-SNAPSHOT"
  id("com.gradleup.shadow") version "9.0.0-beta4"
  id("roundalib") version "0.9.0-SNAPSHOT"
}

// The plugin calls minimize() on the RoundaLib shadow task. Shadow 9.0.0-beta4's
// reachability analysis is stricter than beta2's and strips everything only reached
// reflectively — mixins, GUI widgets, event classes — which leaves a jar whose
// mixin config references classes that are not in it, so the game dies at boot.
// Keep RoundaLib whole.
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowRoundaLibJar") {
  minimize { exclude(dependency("me.roundaround:roundalib:.*")) }
}
