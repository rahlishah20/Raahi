package com.example.data.repository

import com.example.data.datasource.KarachiCommunityFeedbackDataSource
import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.DataSourceType
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.CommunityFeedbackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Backend-ready request structure representing POST /api/community-feedback
 */
data class CommunityFeedbackApiPayload(
    val journeyId: String?,
    val routeId: String?,
    val latitude: Double,
    val longitude: Double,
    val safetyRating: String,
    val contextualTags: List<String>,
    val comment: String?,
    val timestamp: Long
)

class CommunityFeedbackRepositoryImpl(
    private val isBackendAvailable: Boolean = false,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CommunityFeedbackRepository {

    override suspend fun submitFeedback(feedback: CommunityFeedback): Result<Unit> = withContext(ioDispatcher) {
        // Enforce Karachi coordinates bounds
        if (!KarachiCommunityFeedbackDataSource.isWithinKarachi(feedback.latitude, feedback.longitude)) {
            return@withContext Result.failure(
                IllegalArgumentException("Submitted feedback location (${feedback.latitude}, ${feedback.longitude}) is outside Karachi.")
            )
        }

        if (isBackendAvailable) {
            // Future Remote API call via POST /api/community-feedback
            // If backend were active, we'd map to CommunityFeedbackApiPayload and call retrofit service.
        }

        // Prototype / Local Data persistence layer
        val localSubmission = feedback.copy(
            sourceType = if (isBackendAvailable) DataSourceType.REAL else DataSourceType.PROTOTYPE
        )
        KarachiCommunityFeedbackDataSource.addFeedback(localSubmission)
    }

    override fun getFeedbackForRoute(routeId: String): Flow<List<CommunityFeedback>> = flow {
        val list = KarachiCommunityFeedbackDataSource.getFeedbackForRoute(
            routeId = routeId,
            points = emptyList()
        )
        emit(list)
    }.flowOn(ioDispatcher)

    override fun getFeedbackForLocation(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): Flow<List<CommunityFeedback>> = flow {
        val list = KarachiCommunityFeedbackDataSource.getFeedbackForLocation(
            lat = latitude,
            lng = longitude,
            radiusMeters = radiusMeters
        )
        emit(list)
    }.flowOn(ioDispatcher)

    override suspend fun getAggregateForRoute(route: RaahiRoute): CommunitySafetyAggregate = withContext(ioDispatcher) {
        val feedbackList = KarachiCommunityFeedbackDataSource.getFeedbackForRoute(
            routeId = route.id,
            points = route.polylinePoints,
            corridorRadiusMeters = 1200
        )
        KarachiCommunityFeedbackDataSource.aggregateFeedback(feedbackList)
    }

    override suspend fun getAggregateForLocation(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): CommunitySafetyAggregate = withContext(ioDispatcher) {
        val feedbackList = KarachiCommunityFeedbackDataSource.getFeedbackForLocation(
            lat = latitude,
            lng = longitude,
            radiusMeters = radiusMeters
        )
        KarachiCommunityFeedbackDataSource.aggregateFeedback(feedbackList)
    }

    override suspend fun getAllFeedback(): List<CommunityFeedback> = withContext(ioDispatcher) {
        KarachiCommunityFeedbackDataSource.getAllFeedback()
    }
}
