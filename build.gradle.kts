import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.31"
}

group = "com.dustinconrad.loophero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(platform("org.optaplanner:optaplanner-bom:8.4.1.Final"))
    implementation("org.optaplanner:optaplanner-core")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
    kotlinOptions.useIR = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}