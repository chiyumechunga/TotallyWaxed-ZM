package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

@Keep
data class NotificationSettings(
    @get:PropertyName("appointmentReminder")
    val appointmentReminder: ReminderSettings = ReminderSettings(),

    @get:PropertyName("cancellationNotice")
    val cancellationNotice: CancellationPolicySettings = CancellationPolicySettings(),

    @get:PropertyName("confirmationRequired")
    val requiresConfirmation: Boolean = false
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this(ReminderSettings(), CancellationPolicySettings(), false)

    @Keep
    data class ReminderSettings(
        @get:PropertyName("enabled")
        val isEnabled: Boolean = false,

        @get:PropertyName("hoursBeforeAppointment")
        val hoursBefore: Int = 24,

        @get:PropertyName("message")
        val templateMessage: String = ""
    ) {
        constructor() : this(false, 24, "")

        @Exclude
        fun isValid(): Boolean = hoursBefore in 1..168 // 1 hour to 1 week

        @Exclude
        fun getFormattedMessage(time: String): String {
            return templateMessage.replace("{time}", time)
        }
    }

    @Keep
    data class CancellationPolicySettings(
        @get:PropertyName("enabled")
        val isEnabled: Boolean = false,

        @get:PropertyName("minimumHours")
        val minimumNoticeHours: Int = 24
    ) {
        constructor() : this(false, 24)

        @Exclude
        fun isValid(): Boolean = minimumNoticeHours in 1..168
    }

    @Exclude
    fun isValid(): Boolean {
        return listOf(
            appointmentReminder.isValid(),
            cancellationNotice.isValid()
        ).all { it }
    }

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "appointmentReminder" to mapOf(
            "enabled" to appointmentReminder.isEnabled,
            "hoursBeforeAppointment" to appointmentReminder.hoursBefore,
            "message" to appointmentReminder.templateMessage
        ),
        "cancellationNotice" to mapOf(
            "enabled" to cancellationNotice.isEnabled,
            "minimumHours" to cancellationNotice.minimumNoticeHours
        ),
        "confirmationRequired" to requiresConfirmation
    )

    companion object {
        fun fromMap(map: Map<String, Any>): NotificationSettings {
            return NotificationSettings(
                appointmentReminder = parseReminderSettings(map["appointmentReminder"] as? Map<String, Any>),
                cancellationNotice = parseCancellationSettings(map["cancellationNotice"] as? Map<String, Any>),
                requiresConfirmation = map["confirmationRequired"] as? Boolean ?: false
            )
        }

        private fun parseReminderSettings(map: Map<String, Any>?): ReminderSettings {
            return ReminderSettings(
                isEnabled = map?.get("enabled") as? Boolean ?: false,
                hoursBefore = (map?.get("hoursBeforeAppointment") as? Long)?.toInt() ?: 24,
                templateMessage = map?.get("message") as? String ?: ""
            )
        }

        private fun parseCancellationSettings(map: Map<String, Any>?): CancellationPolicySettings {
            return CancellationPolicySettings(
                isEnabled = map?.get("enabled") as? Boolean ?: false,
                minimumNoticeHours = (map?.get("minimumHours") as? Long)?.toInt() ?: 24
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationSettings

        return appointmentReminder == other.appointmentReminder &&
                cancellationNotice == other.cancellationNotice &&
                requiresConfirmation == other.requiresConfirmation
    }

    override fun hashCode(): Int {
        var result = appointmentReminder.hashCode()
        result = 31 * result + cancellationNotice.hashCode()
        result = 31 * result + requiresConfirmation.hashCode()
        return result
    }

    override fun toString(): String {
        return "NotificationSettings(" +
                "Reminder: ${appointmentReminder.isEnabled}, " +
                "Cancellation: ${cancellationNotice.isEnabled}, " +
                "Confirmation: $requiresConfirmation)"
    }
}