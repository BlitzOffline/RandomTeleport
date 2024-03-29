import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.blitzoffline"
version = "1.9.0-Release"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.mattstudios.me/artifactory/public")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://repo.glaremasters.me/repository/bloodshot")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // KOTLIN
    implementation(kotlin("stdlib"))
    // MF COMMANDS
    implementation("me.mattstudios.utils:matt-framework:1.4.6")
    // MF CONFIG
    implementation("me.mattstudios:triumph-config:1.0.5-SNAPSHOT")
    // ADVENTURE
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")

    // PLACEHOLDERAPI
    compileOnly("me.clip:placeholderapi:2.11.1")
    // VAULT
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    // LANDS
    compileOnly("com.github.angeschossen:LandsAPI:5.15.2")
    // WORLDGUARD
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6")
    // GRIEFDEFENDER
    compileOnly("com.griefdefender:api:2.1.0-SNAPSHOT")
    // TownyAdvanced Towny
    compileOnly("com.palmergames.bukkit.towny:towny:0.97.5.8")
    // PAPER
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            javaParameters = true
        }
    }

    withType<ProcessResources> {
        expand("version" to project.version)
    }

    withType<ShadowJar> {
        relocate("kotlin", "com.blitzoffline.randomteleport.libs.kotlin")
        relocate("me.mattstudios.mf", "com.blitzoffline.randomteleport.libs.commands")
        relocate("me.mattstudios.config", "com.blitzoffline.randomteleport.libs.config")
        relocate("net.kyori", "com.blitzoffline.randomteleport.libs.adventure")
        archiveFileName.set("RandomTeleport-${project.version}.jar")
        minimize()
    }
}