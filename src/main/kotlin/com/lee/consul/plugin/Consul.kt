package com.lee.consul.plugin

import com.lee.consul.ConsulFeature
import com.lee.consul.consulMicroService
import io.ktor.server.application.*
import io.ktor.util.*


/**
 * @param configurationPath at your own application.conf, it will be [microservice.config] if:
 * ```
 * microservice{
 *  config {
 *     name = "your-service-name"
 *     host = "your-server-host"
 *     port = "your Consul port,default is 8500"
 *  }
 *
 * }
 * ```
 * usage:
 * ```Kotlin
 * fun Application.configureConsul(){
 *
 *     consul {
 *         config.withHttps(false)
 *             .withConsulBuilder {
 *
 *             }
 *             .withRegistrationBuilder {
 *
 *             }
 *     }
 *}
 * ```
 * learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
 */
@KtorDsl
public fun Application.consul(
    configurationPath: String = "microservice.config",
    configuration: ConsulFeature.() -> Unit
) = install(consulMicroService(configurationPath = configurationPath), configuration)