plugins {
    application
    id("org.beryx.jlink") version "2.26.0"
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    java
    id("java-library")
}

group = "com.half"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.4"
val jomlVersion = "1.10.5"

// Configure Java to use the same version for source and target compatibility
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // JUnit
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // LWJGL with all required modules
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-assimp")
    
    // JOML for math - using the same version as LWJGL 3.3.4
    implementation("org.joml:joml:1.10.5")
    implementation("org.joml:joml-primitives:1.10.0")
    
    // LWJGL Natives for Linux
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-linux")
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-linux")
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux")
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-linux")
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.half.Main")
    
    // Set JVM arguments for the application
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED",
        "-Dorg.lwjgl.libraryPath=${project.buildDir}/libs"
    )
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to "com.half.Main"
        )
    }
    
    // Include all runtime dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<JavaExec>("run") {
    classpath = sourceSets.main.get().runtimeClasspath
    jvmArgs = listOf(
        "-Dorg.lwjgl.libraryPath=${project.buildDir}/libs",
        "--enable-native-access=ALL-UNNAMED"
    )
    dependsOn("build")
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}
tasks.withType<JavaExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
jlink {
    launcher {
        name = "GameEngine"
        jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
    }
    
    jpackage {
        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            installerOptions = listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu", "--win-shortcut")
        } else if (org.gradle.internal.os.OperatingSystem.current().isLinux) {
            installerOptions = listOf("--linux-shortcut")
        } else if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
            installerOptions = listOf("--mac-package-name", "GameEngine")
        }
    }
}