package com.lecezar.rezygen

import SERVER_PORT
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import models.TestResponse

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::mainModule)
        .start(wait = true)
}

fun Application.mainModule() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respond(
                TestResponse(
                    message = "Hello from Ktor!"
                )
            )
        }
    }
}