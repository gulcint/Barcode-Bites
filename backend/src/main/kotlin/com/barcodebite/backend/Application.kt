package com.barcodebite.backend

import com.barcodebite.backend.config.AppConfig
import com.barcodebite.backend.persistence.DatabaseFactory
import com.barcodebite.backend.plugins.configureRouting
import com.barcodebite.backend.plugins.configureSecurity
import com.barcodebite.backend.plugins.configureSerialization
import com.barcodebite.backend.service.createDependencies
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

fun Application.module(appConfig: AppConfig = AppConfig.fromEnvironment()) {
    DatabaseFactory(appConfig.database).init()
    val dependencies = createDependencies(appConfig)

    configureSerialization()
    configureSecurity(appConfig.jwt)
    configureRouting(dependencies)
}
