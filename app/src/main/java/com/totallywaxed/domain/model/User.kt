package  com.totallywaxed.domain.model
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

import java.time.Instant.*

sealed class User {
    abstract val uid: String
    abstract val email: String
    abstract val firstName: String
    abstract val middleName: String?
    abstract val lastName: String
    abstract val createdAt: Instant
    abstract val updatedAt: Instant
    abstract val isActive: Boolean
    abstract val role: Role
}

enum class Role {
    BUSINESS_ADMIN,
    DEVELOPER_ADMIN,
    CLIENT
}

@RequiresApi(Build.VERSION_CODES.O)
data class AdminUser(
    override val uid: String,
    override val email: String,
    override val firstName: String,
    override val middleName: String?,
    override val lastName: String,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val isActive: Boolean,
    override val role: Role,
    val phone: String?,
    val lastLoginAt: Instant?
) : User() {
    init {
        validateUserFields()
        require(role in listOf(Role.BUSINESS_ADMIN, Role.DEVELOPER_ADMIN)) {
            "Invalid admin role: $role"
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class ClientUser(
    override val uid: String,
    override val email: String,
    override val firstName: String,
    override val middleName: String?,
    override val lastName: String,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val isActive: Boolean,
    override val role: Role = Role.CLIENT,
    val phone: String?,
    val notes: String?,
    val preferredServices: List<String>
) : User() {
    init {
        validateUserFields()
        require(preferredServices.all { it.isNotBlank() }) {
            "Preferred services contain blank entries"
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun User.validateUserFields() {
    require(uid.isNotBlank()) { "UID cannot be blank" }
    require(email.isNotBlank() && email.contains("@")) { "Invalid email format: $email" }
    require(firstName.isNotBlank()) { "First name cannot be blank" }
    require(lastName.isNotBlank()) { "Last name cannot be blank" }
    require(createdAt.isBefore(now()) || createdAt == EPOCH) {
        "Created at date cannot be in the future"
    }
}