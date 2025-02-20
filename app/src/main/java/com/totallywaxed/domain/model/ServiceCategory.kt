package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class ServiceCategory(
    @get:PropertyName("id")
    val id: String = "",

    @get:PropertyName("name")
    val name: String = "",

    @get:PropertyName("description")
    val description: String = "",

    @get:PropertyName("displayOrder")
    val displayOrder: Int = 0
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this("", "", "", 0)

    // Utility method to convert to map for Firebase operations
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "displayOrder" to displayOrder
        )
    }

    companion object {
        // Factory method to create from Firebase DataSnapshot
        fun fromMap(map: Map<String, Any>): ServiceCategory {
            return ServiceCategory(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                displayOrder = (map["displayOrder"] as? Long)?.toInt() ?: 0
            )
        }
    }

    // Display-friendly formatted name
    fun getFormattedName(): String {
        return name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    // Validation for category fields
    fun isValid(): Boolean {
        return id.isNotBlank() &&
                name.isNotBlank() &&
                displayOrder >= 0
    }

    override fun toString(): String {
        return "Category: $name (Order: $displayOrder)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceCategory

        return id == other.id &&
                name == other.name &&
                description == other.description &&
                displayOrder == other.displayOrder
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + displayOrder
        return result
    }
}