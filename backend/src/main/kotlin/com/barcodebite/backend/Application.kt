package com.barcodebite.backend

import com.barcodebite.backend.plugins.configureRouting
import com.barcodebite.backend.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = 8080,
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
