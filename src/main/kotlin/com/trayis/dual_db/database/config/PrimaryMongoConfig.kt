package com.trayis.dual_db.database.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(mongoTemplateRef = "primaryMongoTemplate")
class PrimaryMongoConfig : AbstractConfig() {

    @Primary
    @Bean(name = ["primaryMongoTemplate"])
    fun mongoTemplate(@Qualifier("primaryMongoFactory") mongoFactory: MongoDbFactory): MongoTemplate {
        return MongoTemplate(mongoFactory)
    }

    @Primary
    @Bean("primaryMongoFactory")
    @Throws(Exception::class)
    fun mongoFactory(@Qualifier("primaryMongoProperties") mongoProperties: MongoProperties): MongoDbFactory {
        return createDbFactory(mongoProperties)
    }

    @Bean(name = ["primaryMongoProperties"])
    @ConfigurationProperties(prefix = "mongodb.first")
    @Primary
    @Throws(Exception::class)
    fun properties(): MongoProperties {
        return MongoProperties()
    }

}