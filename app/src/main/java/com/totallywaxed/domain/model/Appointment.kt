package com.totallywaxed.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.format.DateTimeParseException

// Appointment class with validation
data class Appointment(
    val id: String,
    val clientEmail: String,
    val clientId: String,
    val clientPhone: String,
    val createdAt: Instant,
    val dateTime: Instant,
    val duration: Int,
    val notes: String?,
    val services: List<AppointmentService>,
    val status: AppointmentStatus,
    val totalPrice: Int,
    val updatedAt: Instant
) {
    init {
        require(duration > 0) { "Duration must be positive" }
        require(totalPrice >= 0) { "Price cannot be negative" }
        require(services.isNotEmpty()) { "Appointment must have at least one service" }
        require(clientPhone.matches(Regex("^\\+?[0-9\\s-]+\$"))) {
            "Invalid phone number format"
        }
    }
}

enum class AppointmentStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

data class AppointmentService(
    val serviceId: String,
    val name: String,
    val duration: Int,
    val price: Int
)

object DateTimeParser {
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseInstant(s: String): Instant {
        return try {
            Instant.parse(s)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid ISO instant format: $s", e)
        }
    }

    fun safeParseInstant(s: String): Instant? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Instant.parse(s)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}

// Safe parsing function with API version check
@RequiresApi(Build.VERSION_CODES.O)
fun parseUserFromJson(json: Map<String, Any>): User {
    val name = json["firstName"] as? String ?: throw IllegalArgumentException("Missing firstName")
    val lastName = json["lastName"] as? String ?: throw IllegalArgumentException("Missing lastName")

    val createdAt = parseDateField(json["createdAt"])
    val updatedAt = parseDateField(json["updatedAt"])

    return when (Role.valueOf((json["role"] as String).uppercase())) {
        Role.CLIENT -> ClientUser(
            uid = json["uid"] as? String ?: throw IllegalArgumentException("Missing uid"),
            email = json["email"] as? String ?: throw IllegalArgumentException("Missing email"),
            firstName = name,
            middleName = json["middleName"] as? String,
            lastName = lastName,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isActive = json["isActive"] as? Boolean ?: false,
            phone = json["phone"] as? String,
            notes = json["notes"] as? String,
            preferredServices = json["preferredServices"] as? List<String> ?: emptyList()
        )
        else -> AdminUser(
            uid = json["uid"] as? String ?: throw IllegalArgumentException("Missing uid"),
            email = json["email"] as? String ?: throw IllegalArgumentException("Missing email"),
            firstName = name,
            middleName = json["middleName"] as? String,
            lastName = lastName,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isActive = json["isActive"] as? Boolean ?: false,
            role = Role.valueOf((json["role"] as String).uppercase()),
            phone = json["phone"] as? String,
            lastLoginAt = (json["lastLoginAt"] as? String)?.let { parseDateField(it) }
        )
    }
}

@Suppress("NewApi")
private fun parseDateField(dateString: Any?): Instant {
    val stringValue = dateString as? String ?: throw IllegalArgumentException("Invalid date format")
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeParser.parseInstant(stringValue)
    } else {
        throw UnsupportedOperationException("Time operations require API level 26+")
    }
}