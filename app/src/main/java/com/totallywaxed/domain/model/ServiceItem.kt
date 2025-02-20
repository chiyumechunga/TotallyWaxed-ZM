package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.util.*

@Keep
data class ServiceItem(
    @get:PropertyName("id")
    val id: String = "",

    @get:PropertyName("categoryId")
    val categoryId: String = "",

    @get:PropertyName("name")
    val name: String = "",

    @get:PropertyName("description")
    val description: String = "",

    @get:PropertyName("price")
    val price: Double = 0.0,

    @get:PropertyName("duration")
    val duration: Int = 0,

    @get:PropertyName("currency")
    val currency: String = "K",

    @get:PropertyName("isActive")
    val isActive: Boolean = true,

    @get:PropertyName("createdAt")
    val createdAt: String = "",

    @get:PropertyName("updatedAt")
    val updatedAt: String = ""
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this("", "", "", "", 0.0, 0, "K", true, "", "")

    @Exclude
    fun isValid(): Boolean = id.isNotBlank() &&
            categoryId.isNotBlank() &&
            name.isNotBlank() &&
            price > 0 &&
            duration > 0

    @Exclude
    fun getFormattedPrice(): String = "%.2f %s".format(price, currency)

    @Exclude
    fun getFormattedDuration(): String = when {
        duration >= 60 -> "${duration / 60}h ${duration % 60}m"
        else -> "${duration}m"
    }

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "categoryId" to categoryId,
        "name" to name,
        "description" to description,
        "price" to price,
        "duration" to duration,
        "currency" to currency,
        "isActive" to isActive,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromMap(map: Map<String, Any>): ServiceItem = ServiceItem(
            id = map["id"]?.toString() ?: "",
            categoryId = map["categoryId"]?.toString() ?: "",
            name = map["name"]?.toString() ?: "",
            description = map["description"]?.toString() ?: "",
            price = (map["price"] as? Double) ?: 0.0,
            duration = (map["duration"] as? Long)?.toInt() ?: 0,
            currency = map["currency"]?.toString() ?: "K",
            isActive = map["isActive"] as? Boolean ?: true,
            createdAt = map["createdAt"]?.toString() ?: "",
            updatedAt = map["updatedAt"]?.toString() ?: ""
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceItem

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "ServiceItem(id='$id', name='$name', category='$categoryId')"
}