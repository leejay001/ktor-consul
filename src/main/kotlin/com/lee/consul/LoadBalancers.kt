package com.lee.consul

import com.orbitz.consul.model.health.ServiceHealth

typealias LoadBalancer = List<ServiceHealth>.() -> ServiceHealth?

fun isHealthy(health: ServiceHealth): Boolean {
    return health.checks.all { check ->
        check.status == "passing"
    }
}

fun takeFirstHealthy(): LoadBalancer = {
    firstOrNull(::isHealthy)
}

fun roundRobin(): LoadBalancer {
    var index = 0
    return {
        getOrNull(index)?.also {
            index = (index + 1) % size
        }
    }
}