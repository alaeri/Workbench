plugins {
    kotlin("jvm") //version "1.7.20"
    id("org.jetbrains.compose") //version "1.2.2"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.zachklipp.seqdiag:seqdiag:0.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(project(":log-serialize"))
    implementation(project(":log-extras"))
    implementation(project(":log-core"))
    implementation(project(":log-sample-lib"))
    implementation(project(":log-server"))
    implementation(project(":log-repository"))
}

compose.desktop {
    application {
        mainClass = "com.alaeri.seqdiag.MainKt"
    }
}