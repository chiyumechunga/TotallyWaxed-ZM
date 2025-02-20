package com.totallywaxed.data.repository

import com.totallywaxed.data.remote.FirebaseStorageDataSource
import com.totallywaxed.domain.model.Service
import com.totallywaxed.domain.model.ServiceFactory
import com.totallywaxed.core.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val firebaseDataSource: FirebaseStorageDataSource
) {

    /**
     * Fetches all active services from the data source.
     */
    suspend fun getActiveServices(): Result<List<Service>> {
        return try {
            val serviceItems = firebaseDataSource.getActiveServices()
            val services = serviceItems.map { serviceItem ->
                ServiceFactory.createService(
                    id = serviceItem.id,
                    name = serviceItem.name,
                    description = serviceItem.description,
                    price = serviceItem.price,
                    duration = serviceItem.duration,
                    currency = serviceItem.currency,
                    isActive = serviceItem.isActive,
                    createdAt = Date(), // Replace with actual createdAt from serviceItem
                    updatedAt = Date(), // Replace with actual updatedAt from serviceItem
                    categoryId = serviceItem.categoryId,
                    additionalProperties = emptyMap() // Add additional properties if needed
                )
            }
            Result.Success(services)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetches services by category.
     */
    suspend fun getServicesByCategory(categoryId: String): Result<List<Service>> {
        return try {
            val allServices = getActiveServices()
            when (allServices) {
                is Result.Success -> {
                    val filteredServices = allServices.data.filter { service ->
                        when (service) {
                            is StandardService -> categoryId == "cat_001"
                            is PremiumService -> categoryId == "cat_002"
                            is FacialService -> categoryId == "cat_003"
                            else -> false
                        }
                    }
                    Result.Success(filteredServices)
                }
                is Result.Error -> allServices
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Observes changes in services (realtime updates).
     */
    fun observeServices(): Flow<List<Service>> {
        return firebaseDataSource.observeServices().map { serviceItems ->
            serviceItems.map { serviceItem ->
                ServiceFactory.createService(
                    id = serviceItem.id,
                    name = serviceItem.name,
                    description = serviceItem.description,
                    price = serviceItem.price,
                    duration = serviceItem.duration,
                    currency = serviceItem.currency,
                    isActive = serviceItem.isActive,
                    createdAt = Date(), // Replace with actual createdAt from serviceItem
                    updatedAt = Date(), // Replace with actual updatedAt from serviceItem
                    categoryId = serviceItem.categoryId,
                    additionalProperties = emptyMap() // Add additional properties if needed
                )
            }
        }
    }
}