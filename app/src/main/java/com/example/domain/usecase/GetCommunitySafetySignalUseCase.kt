package com.example.domain.usecase

import com.example.data.repository.CommunityFeedbackRepositoryImpl
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.CommunityFeedbackRepository

class GetCommunitySafetySignalUseCase(
    private val repository: CommunityFeedbackRepository = CommunityFeedbackRepositoryImpl()
) {
    suspend operator fun invoke(route: RaahiRoute): CommunitySafetyAggregate {
        return repository.getAggregateForRoute(route)
    }

    suspend fun getForLocation(latitude: Double, longitude: Double, radiusMeters: Int = 2000): CommunitySafetyAggregate {
        return repository.getAggregateForLocation(latitude, longitude, radiusMeters)
    }
}
