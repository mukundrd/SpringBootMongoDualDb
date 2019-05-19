package com.trayis.dual_db.database.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(mongoTemplateRef = "secondaryMongoTemplate")
class SecondaryMongoConfig : AbstractConfig() {

    @Bean(name = ["secondaryMongoTemplate"])
    fun mongoTemplate(@Qualifier("secondaryMongoFactory") mongoFactory: MongoDbFactory): MongoTemplate {
        return MongoTemplate(mongoFactory)
    }

    @Bean("secondaryMongoFactory")
    @Throws(Exception::class)
    fun mongoFactory(@Qualifier("secondaryMongoProperties") mongoProperties: MongoProperties): MongoDbFactory {
        return createDbFactory(mongoProperties)
    }

    @Bean(name = ["secondaryMongoProperties"])
    @ConfigurationProperties(prefix = "mongodb.second")
    @Throws(Exception::class)
    fun properties(): MongoProperties {
        return MongoProperties()
    }
}