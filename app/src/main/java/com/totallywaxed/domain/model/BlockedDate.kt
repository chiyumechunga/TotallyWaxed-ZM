package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class BlockedDate(
    @get:PropertyName("id")
    val id: String = "",

    @get:PropertyName("date")
    val date: String = "",

    @get:PropertyName("reason")
    val reason: String = "",

    @get:PropertyName("createdBy")
    val createdBy: String = "",

    @get:PropertyName("createdAt")
    val createdAt: String = "",

    @get:PropertyName("updatedAt")
    val updatedAt: String = ""
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this("", "", "", "", "", "")

    @Exclude
    fun isValid(): Boolean = id.isNotBlank() &&
            isValidDate() &&
            reason.isNotBlank() &&
            createdBy.isNotBlank()

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "date" to date,
        "reason" to reason,
        "createdBy" to createdBy,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    @Exclude
    fun getFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            date // Return original if parsing fails
        }
    }

    @Exclude
    fun getCreationDate(): Date? = parseDateString(createdAt)

    @Exclude
    fun getUpdateDate(): Date? = parseDateString(updatedAt)

    @Exclude
    private fun parseDateString(dateString: String): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    @Exclude
    private fun isValidDate(): Boolean {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        fun fromMap(map: Map<String, Any>): BlockedDate = BlockedDate(
            id = map["id"]?.toString() ?: "",
            date = map["date"]?.toString() ?: "",
            reason = map["reason"]?.toString() ?: "",
            createdBy = map["createdBy"]?.toString() ?: "",
            createdAt = map["createdAt"]?.toString() ?: "",
            updatedAt = map["updatedAt"]?.toString() ?: ""
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockedDate

        return id == other.id &&
                date == other.date &&
                reason == other.reason &&
                createdBy == other.createdBy &&
                createdAt == other.createdAt &&
                updatedAt == other.updatedAt
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + reason.hashCode()
        result = 31 * result + createdBy.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "BlockedDate(id='$id', date='${getFormattedDate()}', reason='$reason')"
    }
}