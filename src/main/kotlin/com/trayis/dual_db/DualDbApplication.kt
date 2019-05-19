package com.trayis.dual_db

import com.trayis.dual_db.database.primary.PrimaryModel
import com.trayis.dual_db.database.secondary.SecondaryModel
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootApplication
class DualDbApplication : CommandLineRunner {

    val log = LogFactory.getLog(DualDbApplication::class.java)

    @Autowired
    @Qualifier(value = "primaryMongoTemplate")
    private val primaryTemplate: MongoTemplate? = null

    @Autowired
    @Qualifier(value = "secondaryMongoTemplate")
    private val secondaryTemplate: MongoTemplate? = null

    @Throws(Exception::class)
    override fun run(vararg args: String) {

        log.info("************************************************************")
        log.info("Start printing mongo objects")
        log.info("************************************************************")

        this.primaryTemplate?.save(PrimaryModel(value = "Primary database plain object"))
        this.secondaryTemplate?.save(SecondaryModel(value = "Secondary database plain object"))

        this.primaryTemplate?.apply {
            val primaries = findAll(PrimaryModel::class.java)
            for (primary in primaries) {
                log.info(primary)
            }
        }

        this.secondaryTemplate?.apply {
            val secondaries = findAll(SecondaryModel::class.java)
            for (secondary in secondaries) {
                log.info(secondary)
            }
        }

        log.info("************************************************************")
        log.info("Ended printing mongo objects")
        log.info("************************************************************")

    }

}

fun main(args: Array<String>) {
    SpringApplication.run(DualDbApplication::class.java, *args)
}