import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/////////////////
// ATTN: CHANGE ME
val starsectorDirectory = "C:/Program Files (x86)/Fractal Softworks/Starsector"
val jarFileName = "My_Mod.jar"
/////////////////

val starsectorCoreDirectory = "$starsectorDirectory/starsector-core"
val starsectorModDirectory = "$starsectorDirectory/mods"

plugins {
    kotlin("jvm") version "1.3.50"
    java
}

version = "1.0.0"

repositories {
    maven(url = uri("$projectDir/libs"))
    jcenter()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk7"))

    // We set up the Starsector API jarfile as a local Maven dependency (using the Maven folder structure)
    // so that Gradle can import the Starsector API with the actual source code available to it.
    // ATTN: You must add this, as described in the readme.md file.
    compileOnly("starfarer:starfarer-api:1.0")

    // Also import the rest of the Starsector jar dependencies (except for the API, which we do specially above)
    compileOnly(fileTree(starsectorCoreDirectory) {
        include("*.jar")
        exclude("*_obf.jar", "starfarer.api.jar") // If you just want to compile the mod and do not care about viewing sources, comment this line out
    })

    // It is recommended to require LazyLib, as it will provide the Kotlin runtime.
    compileOnly(fileTree("$starsectorModDirectory/LazyLib/jars") { include("*.jar") })

    // Unit testing libraries. Good to use, but not necessary.
//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
//    testImplementation("org.assertj:assertj-core:3.11.1")
//    testImplementation("io.mockk:mockk:1.9")
}

tasks {
    named<Jar>("jar")
    {
        destinationDir = file("$rootDir/jars")
        archiveName = jarFileName
    }

    register("debug-starsector", Exec::class) {
        println("Starting debugger for Starsector...")
        workingDir = file(starsectorCoreDirectory)


        commandLine = if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            listOf("cmd", "/C", "debug-starsector.bat")
        } else {
            listOf("./starsectorDebug.bat")
        }
    }

    register("run-starsector", Exec::class) {
        println("Starting Starsector...")
        workingDir = file(starsectorCoreDirectory)

        commandLine = if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            listOf("cmd", "/C", "starsector.bat")
        } else {
            listOf("./starsector.bat")
        }
    }
}

// Compile to Java 6 bytecode so that Starsector can use it
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.6"
}