plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
    application
}

group = "com.lecezar.rezygen"
version = "1.0.0"
application {
    mainClass.set("com.lecezar.rezygen.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    implementation(libs.ktor.server.contentNegotiation.jvm)
    implementation(libs.ktor.serialization.json.jvm)

    //test
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}