package com.lee.consul

import com.lee.consul.Config.Companion.DEFAULT_CONSUL_URL
import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import io.ktor.server.application.*
import io.ktor.server.config.*

class ConsulFeature(
    val config: Config
) {

    init {
        val (serviceName, host, port, isHttps, consulUrl, configConsul, registrationConfig) = config
        val consulClient = Consul.builder()
            .withHttps(isHttps)
            .withUrl(consulUrl)
            .apply(configConsul)
            .build()

        val service = ImmutableRegistration.builder()
            .id("$serviceName-$port")
            .name(serviceName)
            .address(host)
            .port(port)
            .apply(registrationConfig)
            .build()

        consulClient.agentClient().register(service)
    }

}


class Config(
    var serviceName: String,
    var host: String,
    var port: Int,
    var consulUrl: String = DEFAULT_CONSUL_URL
) {
    private var configConsul: Consul.Builder.() -> Unit = {}
    private var registrationConfig: ImmutableRegistration.Builder.() -> Unit = {}
    private var configChange: Config.() -> Unit = {}

    var isHttps = false

    fun withConfig(configChange: Config.() -> Unit): Config{
        this.configChange = configChange
        return this
    }

    fun withHttps(isHttps: Boolean = false): Config {
        this.isHttps = isHttps
        return this
    }

    fun withConsulBuilder(configConsul: Consul.Builder.() -> Unit): Config {
        this.configConsul = configConsul
        return this
    }

    fun withRegistrationBuilder(registrationConfig: ImmutableRegistration.Builder.() -> Unit): Config {
        this.registrationConfig = registrationConfig
        return this
    }

    internal operator fun component1() = serviceName
    internal operator fun component2() = host
    internal operator fun component3() = port
    internal operator fun component4() = isHttps

    internal operator fun component5() = consulUrl
    internal operator fun component6() = configConsul
    internal operator fun component7() = registrationConfig

    companion object{
        const val DEFAULT_CONSUL_URL = "http://localhost:8500"
    }

}


/**
 * @param configurationPath at your own application.conf, it will be microservice.config if:
 * microservice{
 *  config {
 *     name = "your-service-name"
 *     host = "your-server-host"
 *     port = "your Consul port,default is 8500"
 *  }
 *
 * }
 * learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
 */
fun consulMicroService(
    pluginName: String = "ConsulPlugin",
    configurationPath: String = "microservice.config"
): ApplicationPlugin<ConsulFeature> = createApplicationPlugin(
    name = pluginName,
    configurationPath = configurationPath,

    createConfiguration = { applicationConfig ->

        val name = applicationConfig.tryGetString("name") ?: "default"
        val host = applicationConfig.tryGetString("host") ?: "localhost"
        val port = applicationConfig.tryGetString("port")?.toInt() ?: 8080

        val consulUrl = applicationConfig.tryGetString("consul_url") ?: DEFAULT_CONSUL_URL


        ConsulFeature(
            Config(
                serviceName = name,
                host = host,
                port = port,
                consulUrl = consulUrl
            )
        )
    },
    body = {}
)