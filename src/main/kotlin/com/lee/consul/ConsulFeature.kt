package com.lee.consul

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
    var port: Int
) {
    private var configConsul: Consul.Builder.() -> Unit = {}
    private var registrationConfig: ImmutableRegistration.Builder.() -> Unit = {}

    var isHttps = false
    val consulUrl: String get() = if (isHttps) "https://$host:$port" else "http://$host:$port"

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

        val name = applicationConfig.tryGetString("$configurationPath.name") ?: "account"
        val host = applicationConfig.tryGetString("$configurationPath.host") ?: "localhost"
        val port = applicationConfig.tryGetString("$configurationPath.port")?.toInt() ?: 8500

        ConsulFeature(
            Config(
                serviceName = name,
                host = host,
                port = port
            )
        )
    },
    body = {}
)