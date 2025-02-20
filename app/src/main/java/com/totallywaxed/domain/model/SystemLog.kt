package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class SystemLog(
    @Exclude // Not stored in Firebase data, only used as key
    val id: String = "",

    @get:PropertyName("action")
    val actionType: String = "",

    @get:PropertyName("details")
    val description: String = "",

    @get:PropertyName("entity")
    val targetEntity: String = "",

    @get:PropertyName("entityId")
    val entityIdentifier: String = "",

    @get:PropertyName("performedBy")
    val actorId: String = "",

    @get:PropertyName("timestamp")
    val eventTime: String = ""
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this("", "", "", "", "", "", "")

    @Exclude
    fun isValid(): Boolean = listOf(
        actionType.isNotBlank(),
        targetEntity.isNotBlank(),
        entityIdentifier.isNotBlank(),
        actorId.isNotBlank(),
        isValidTimestamp()
    ).all { it }

    @Exclude
    fun toMap(): Map<String, Any> = mapOf(
        "action" to actionType,
        "details" to description,
        "entity" to targetEntity,
        "entityId" to entityIdentifier,
        "performedBy" to actorId,
        "timestamp" to eventTime
    )

    @Exclude
    fun getFormattedTimestamp(): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
            val formatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm z", Locale.getDefault())
            val parsedDate = parser.parse(eventTime)
            formatter.format(parsedDate)
        } catch (e: Exception) {
            "Invalid timestamp"
        }
    }

    @Exclude
    private fun isValidTimestamp(): Boolean {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(eventTime)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        fun fromFirebaseEntry(id: String, map: Map<String, Any>): SystemLog {
            return SystemLog(
                id = id,
                actionType = map["action"]?.toString() ?: "",
                description = map["details"]?.toString() ?: "",
                targetEntity = map["entity"]?.toString() ?: "",
                entityIdentifier = map["entityId"]?.toString() ?: "",
                actorId = map["performedBy"]?.toString() ?: "",
                eventTime = map["timestamp"]?.toString() ?: ""
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SystemLog

        return id == other.id &&
                actionType == other.actionType &&
                description == other.description &&
                targetEntity == other.targetEntity &&
                entityIdentifier == other.entityIdentifier &&
                actorId == other.actorId &&
                eventTime == other.eventTime
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + actionType.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + targetEntity.hashCode()
        result = 31 * result + entityIdentifier.hashCode()
        result = 31 * result + actorId.hashCode()
        result = 31 * result + eventTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "SystemLog[$id] $actionType on $targetEntity ($entityIdentifier) by $actorId"
    }
}