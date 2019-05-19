package com.trayis.dual_db.database.config

import com.mongodb.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.SimpleMongoDbFactory

abstract class AbstractConfig {

    private lateinit var properties: MongoProperties

    @Autowired
    private val environment: Environment? = null

    private fun hasCustomAddress() = properties.host != null || properties.port != null

    private fun hasCustomCredentials() = properties.username != null && properties.password != null

    private fun builder(options: MongoClientOptions?) = options?.let { MongoClientOptions.builder(options) }
            ?: MongoClientOptions.builder()

    private fun determineUri() = properties.uri ?: "mongodb://localhost/test"

    private fun getMongoClientDatabase() = properties.database ?: (MongoClientURI(determineUri())).database!!

    private fun clearPassword() {
        properties.password?.let {
            for (i in 0 until it.size) {
                it[i] = 0.toChar()
            }
        }
    }

    fun createDbFactory(mongoProperties: MongoProperties): MongoDbFactory {
        properties = mongoProperties
        return SimpleMongoDbFactory(
                createMongoClient(MongoClientOptions.builder().build(), environment),
                mongoProperties.database)
    }

    private fun createMongoClient(options: MongoClientOptions, environment: Environment?): MongoClient {
        try {
            val embeddedPort = getEmbeddedPort(environment) ?: return createNetworkMongoClient(options)
            return createEmbeddedMongoClient(options, embeddedPort)
        } finally {
            clearPassword()
        }
    }

    private fun createEmbeddedMongoClient(options: MongoClientOptions?, port: Int): MongoClient {
        val lOptions = options ?: MongoClientOptions.builder().build()
        val host = properties.host ?: "localhost"
        return MongoClient(listOf(ServerAddress(host, port)), lOptions)
    }

    private fun getEmbeddedPort(environment: Environment?): Int? {
        environment?.getProperty("local.mongo.port")?.let {
            return Integer.valueOf(it)
        }
        return null
    }

    private fun createNetworkMongoClient(options: MongoClientOptions?): MongoClient {
        if (!hasCustomAddress() && !hasCustomCredentials()) {
            return MongoClient(MongoClientURI(determineUri(), builder(options)))
        }

        if (properties.uri != null) {
            throw IllegalStateException("Invalid mongo configuration, either uri or host/port/credentials must be specified")
        }

        val lOptions = options ?: MongoClientOptions.builder().build()

        var credentials: MongoCredential? = null
        var host: String? = null
        if (hasCustomCredentials()) {
            host = properties.authenticationDatabase ?: getMongoClientDatabase()
            credentials = MongoCredential.createCredential(properties.username, host, properties.password)
        }

        host = properties.host ?: "localhost"
        val port = properties.port ?: 27017
        return MongoClient(mutableListOf(ServerAddress(host, port)), credentials!!, lOptions)
    }

}
