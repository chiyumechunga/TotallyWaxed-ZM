package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class BusinessSettings(
    @get:PropertyName("name")
    val businessName: String = "",

    @get:PropertyName("address")
    val address: String = "",

    @get:PropertyName("email")
    val email: String = "",

    @get:PropertyName("phone")
    val phone: String = "",

    @get:PropertyName("currency")
    val currency: String = "K",

    @get:PropertyName("timezone")
    val timezone: String = "Africa/Lusaka",

    @get:PropertyName("appointmentBuffer")
    val appointmentBufferMinutes: Int = 15,

    @get:PropertyName("cancellationPolicy")
    val cancellationPolicy: String = "",

    @get:PropertyName("updatedAt")
    val lastUpdated: String = ""
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this("", "", "", "", "K", "Africa/Lusaka", 15, "", "")

    @Exclude
    fun isValid(): Boolean = listOf(
        businessName.isNotBlank(),
        email.isNotEmpty(),
        timezone.isNotEmpty(),
        appointmentBufferMinutes >= 0
    ).all { it }

    @Exclude
    fun getFormattedAddress(): String = address.replace(", ", ",\n")

    @Exclude
    fun getFormattedContactInfo(): String = """
        Phone: $phone
        Email: $email
    """.trimIndent()

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to businessName,
        "address" to address,
        "email" to email,
        "phone" to phone,
        "currency" to currency,
        "timezone" to timezone,
        "appointmentBuffer" to appointmentBufferMinutes,
        "cancellationPolicy" to cancellationPolicy,
        "updatedAt" to lastUpdated
    )

    @Exclude
    fun getLastUpdatedDate(): Date? = try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(lastUpdated)
    } catch (e: Exception) {
        null
    }

    @Exclude
    fun isValidTimeZone(): Boolean = try {
        TimeZone.getTimeZone(timezone).id == timezone
    } catch (e: Exception) {
        false
    }

    companion object {
        fun fromMap(map: Map<String, Any>): BusinessSettings = BusinessSettings(
            businessName = map["name"]?.toString() ?: "",
            address = map["address"]?.toString() ?: "",
            email = map["email"]?.toString() ?: "",
            phone = map["phone"]?.toString() ?: "",
            currency = map["currency"]?.toString() ?: "K",
            timezone = map["timezone"]?.toString() ?: "Africa/Lusaka",
            appointmentBufferMinutes = (map["appointmentBuffer"] as? Long)?.toInt() ?: 15,
            cancellationPolicy = map["cancellationPolicy"]?.toString() ?: "",
            lastUpdated = map["updatedAt"]?.toString() ?: ""
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusinessSettings

        return businessName == other.businessName &&
                address == other.address &&
                email == other.email &&
                phone == other.phone &&
                currency == other.currency &&
                timezone == other.timezone &&
                appointmentBufferMinutes == other.appointmentBufferMinutes &&
                cancellationPolicy == other.cancellationPolicy &&
                lastUpdated == other.lastUpdated
    }

    override fun hashCode(): Int {
        var result = businessName.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + timezone.hashCode()
        result = 31 * result + appointmentBufferMinutes
        result = 31 * result + cancellationPolicy.hashCode()
        result = 31 * result + lastUpdated.hashCode()
        return result
    }

    override fun toString(): String {
        return "BusinessSettings(name='$businessName', currency='$currency', tz='$timezone')"
    }
}