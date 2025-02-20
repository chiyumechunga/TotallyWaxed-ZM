package com.totallywaxed.domain.model

import java.util.Date

open class Service(
    open val id: String,
    open val name: String,
    open val description: String,
    open val price: Double,
    open val duration: Int,
    open val currency: String,
    open val isActive: Boolean,
    open val createdAt: Date,
    open val updatedAt: Date
) {
    /**
     * Checks if the service is currently active.
     */
    fun isAvailable(): Boolean {
        return isActive
    }

    /**
     * Calculates the total cost for a given quantity of this service.
     */
    fun calculateTotalCost(quantity: Int): Double {
        return price * quantity
    }

    override fun toString(): String {
        return "Service(id='$id', name='$name', price=$price, duration=$duration, currency='$currency')"
    }
}

class StandardService(
    override val id: String,
    override val name: String,
    override val description: String,
    override val price: Double,
    override val duration: Int,
    override val currency: String,
    override val isActive: Boolean,
    override val createdAt: Date,
    override val updatedAt: Date,
    val area: String // Specific to standard services (e.g., "legs", "arms")
) : Service(id, name, description, price, duration, currency, isActive, createdAt, updatedAt) {

    /**
     * Returns a description of the service area.
     */
    fun getServiceAreaDescription(): String {
        return "Service area: $area"
    }
}

class PremiumService(
    override val id: String,
    override val name: String,
    override val description: String,
    override val price: Double,
    override val duration: Int,
    override val currency: String,
    override val isActive: Boolean,
    override val createdAt: Date,
    override val updatedAt: Date,
    val isFullBody: Boolean // Specific to premium services
) : Service(id, name, description, price, duration, currency, isActive, createdAt, updatedAt) {

    /**
     * Returns whether this is a full-body service.
     */
    fun isFullBodyService(): Boolean {
        return isFullBody
    }
}

class FacialService(
    override val id: String,
    override val name: String,
    override val description: String,
    override val price: Double,
    override val duration: Int,
    override val currency: String,
    override val isActive: Boolean,
    override val createdAt: Date,
    override val updatedAt: Date,
    val facialArea: String // Specific to facial services (e.g., "upper lip", "chin")
) : Service(id, name, description, price, duration, currency, isActive, createdAt, updatedAt) {

    /**
     * Returns the specific facial area for this service.
     */
    fun getFacialArea(): String {
        return facialArea
    }
}
