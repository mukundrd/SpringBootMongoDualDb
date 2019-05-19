package com.trayis.dual_db.database.primary

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "first_mongo")
data class PrimaryModel(var _id: String? = null, var value: String)
