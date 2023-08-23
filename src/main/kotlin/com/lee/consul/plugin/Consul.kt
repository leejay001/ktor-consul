package com.lee.consul.plugin

import com.lee.consul.ConsulClientFeature
import com.lee.consul.ConsulFeature
import com.lee.consul.consulConnectClientPlugin
import com.lee.consul.consulMicroService
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.server.application.*
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


/**
 *connect microservice
 * @param engineFactory see: https://ktor.io/docs/create-client.html
 * @param serviceName will be : your-service-name set before you want to connect
 * @param consulUrl is where your Consul launched
 * @param configuration config more
 */
@KtorDsl
public  fun <T : HttpClientEngineConfig> consulConnect(
    engineFactory: HttpClientEngineFactory<T>,
    serviceName: String,
    consulUrl: String = "http://localhost:8500",
    configuration: ConsulClientFeature.() -> Unit
) = HttpClient(engineFactory){
    install(consulConnectClientPlugin(serviceName,consulUrl),configuration)
}