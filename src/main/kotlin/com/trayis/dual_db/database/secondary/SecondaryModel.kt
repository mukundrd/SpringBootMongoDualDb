package com.trayis.dual_db.database.secondary

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "second_mongo")
data class SecondaryModel(var _id: String? = null, var value: String)