group = "com.jsv"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.9.0"
    id("java-gradle-plugin")
}

gradlePlugin {
    plugins {
        create("prcheck") {
            id = "com.jsv.prcheck"
            implementationClass = "com.jsv.PrCheckPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}