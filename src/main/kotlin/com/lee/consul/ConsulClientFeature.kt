package com.lee.consul

import com.orbitz.consul.Consul
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*

class ConsulClientFeature(val config: ClientConfig)

class ClientConfig(
    var loadBalancer: LoadBalancer = takeFirstHealthy(),
    var config: Consul.Builder.() -> Unit = {},
    var consulUrl: String = "",
    var serviceName: String? = null

) {

    fun withBalancer(loadBalancer: LoadBalancer) {
        this.loadBalancer = loadBalancer
    }

    fun withConfig(config: Consul.Builder.() -> Unit) {
        this.config = config
    }

}

fun consulConnectClientPlugin(
    serviceName: String,
    consulUrl: String = "http://localhost:8500"
) = createClientPlugin(
    name = "ConsulPluginClient",
    createConfiguration ={
        val clientConfig = ClientConfig(consulUrl = consulUrl, serviceName = serviceName)
        ConsulClientFeature(clientConfig)
    },
    body ={
        val config = pluginConfig.config
        client.requestPipeline.intercept(HttpRequestPipeline.Render) {
            val consulClient = Consul.builder().withUrl(config.consulUrl).apply(config.config).build()
            val nodes = consulClient.healthClient().getHealthyServiceInstances(serviceName).response
            val selectedNode = checkNotNull(config.loadBalancer(nodes)) {
                "Impossible to find available nodes of the $serviceName"
            }

            val serviceHost = selectedNode.service.address.host
            this.context.url.host = serviceHost
            this.context.url.port = selectedNode.service.port

        }
    }
)

private inline val String.host
    get() =
        removePrefix("https://")
            .removePrefix("http://")

