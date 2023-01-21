plugins {
    kotlin("jvm") //version "1.7.20"
    id("org.jetbrains.compose") //version "1.2.2"
    kotlin("plugin.serialization")// version "1.8.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.zachklipp.seqdiag:seqdiag:0.2.0")
    implementation("io.ktor:ktor-client-core:2.2.2")
    implementation("io.ktor:ktor-client-cio:2.2.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.mikepenz:multiplatform-markdown-renderer:0.6.1")

    implementation(project(":log-serialize"))
    implementation(project(":log-extras"))
    implementation(project(":log-core"))
    implementation(project(":log-repository"))
}

compose.desktop {
    application {
        mainClass = "com.alaeri.seqdiag.MainKt"
    }
}