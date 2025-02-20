package com.totallywaxed.domain.model


import java.util.Date

/**
 * Factory class to create Service instances based on the category.
 */
object ServiceFactory {

    /**
     * Creates a Service instance based on the category ID.
     */
    fun createService(
        id: String,
        name: String,
        description: String,
        price: Double,
        duration: Int,
        currency: String,
        isActive: Boolean,
        createdAt: Date,
        updatedAt: Date,
        categoryId: String,
        additionalProperties: Map<String, Any> = emptyMap()
    ): Service {
        return when (categoryId) {
            "cat_001" -> StandardService(
                id, name, description, price, duration, currency, isActive, createdAt, updatedAt,
                additionalProperties["area"] as? String ?: "unknown"
            )
            "cat_002" -> PremiumService(
                id, name, description, price, duration, currency, isActive, createdAt, updatedAt,
                additionalProperties["isFullBody"] as? Boolean ?: false
            )
            "cat_003" -> FacialService(
                id, name, description, price, duration, currency, isActive, createdAt, updatedAt,
                additionalProperties["facialArea"] as? String ?: "unknown"
            )
            else -> throw IllegalArgumentException("Unknown category ID: $categoryId")
        }
    }
}