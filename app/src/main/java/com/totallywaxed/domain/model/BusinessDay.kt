package com.totallywaxed.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class BusinessDay(
    @get:PropertyName("isOpen")
    val isOpen: Boolean = false,

    @get:PropertyName("start")
    val start: String? = null,

    @get:PropertyName("end")
    val end: String? = null,

    @get:PropertyName("breakStart")
    val breakStart: String? = null,

    @get:PropertyName("breakEnd")
    val breakEnd: String? = null
) {
    // Secondary constructor for Firebase deserialization
    constructor() : this(false, null, null, null, null)

    @Exclude
    fun isValid(): Boolean {
        if (!isOpen) return true
        return !(start.isNullOrBlank() || end.isNullOrBlank())
    }

    @Exclude
    fun hasBreak(): Boolean {
        return !breakStart.isNullOrBlank() && !breakEnd.isNullOrBlank()
    }

    @Exclude
    fun getFormattedHours(): String {
        if (!isOpen) return "Closed"
        return "$start - $end" + if (hasBreak()) " (Break: $breakStart - $breakEnd)" else ""
    }

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "isOpen" to isOpen,
        "start" to start,
        "end" to end,
        "breakStart" to breakStart,
        "breakEnd" to breakEnd
    )

    @Exclude
    fun isTimeWithinBusinessHours(time: String): Boolean {
        if (!isOpen) return false

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val targetTime = timeFormat.parse(time) ?: return false
        val startTime = start?.let { timeFormat.parse(it) } ?: return false
        val endTime = end?.let { timeFormat.parse(it) } ?: return false

        return !targetTime.before(startTime) && !targetTime.after(endTime)
    }

    companion object {
        fun fromMap(map: Map<String, Any>): BusinessDay = BusinessDay(
            isOpen = map["isOpen"] as? Boolean ?: false,
            start = map["start"] as? String,
            end = map["end"] as? String,
            breakStart = map["breakStart"] as? String,
            breakEnd = map["breakEnd"] as? String
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusinessDay

        return isOpen == other.isOpen &&
                start == other.start &&
                end == other.end &&
                breakStart == other.breakStart &&
                breakEnd == other.breakEnd
    }

    override fun hashCode(): Int {
        var result = isOpen.hashCode()
        result = 31 * result + (start?.hashCode() ?: 0)
        result = 31 * result + (end?.hashCode() ?: 0)
        result = 31 * result + (breakStart?.hashCode() ?: 0)
        result = 31 * result + (breakEnd?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "BusinessDay(isOpen=$isOpen, start=$start, end=$end, break=$breakStart-$breakEnd)"
    }
}