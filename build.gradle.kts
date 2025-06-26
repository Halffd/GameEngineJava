plugins {
    id("java")
    application
}

group = "com.half"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Use Maven LWJGL (since your local build has no JARs)
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.4"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")

    // Use Maven natives too (your local ones might be incompatible)
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-linux")
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-linux")
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.half.Main")
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED"
    )
}