package com.example.domain.repository

import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.RaahiRoute
import kotlinx.coroutines.flow.Flow

interface CommunityFeedbackRepository {
    suspend fun submitFeedback(feedback: CommunityFeedback): Result<Unit>
    fun getFeedbackForRoute(routeId: String): Flow<List<CommunityFeedback>>
    fun getFeedbackForLocation(latitude: Double, longitude: Double, radiusMeters: Int): Flow<List<CommunityFeedback>>
    suspend fun getAggregateForRoute(route: RaahiRoute): CommunitySafetyAggregate
    suspend fun getAggregateForLocation(latitude: Double, longitude: Double, radiusMeters: Int): CommunitySafetyAggregate
    suspend fun getAllFeedback(): List<CommunityFeedback>
}
