package com.example

import com.example.data.repository.CommunityFeedbackRepositoryImpl
import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.usecase.SubmitCommunityFeedbackUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class SubmitCommunityFeedbackUseCaseTest {

    private lateinit var repository: CommunityFeedbackRepositoryImpl
    private lateinit var useCase: SubmitCommunityFeedbackUseCase

    @Before
    fun setUp() {
        repository = CommunityFeedbackRepositoryImpl()
        useCase = SubmitCommunityFeedbackUseCase(repository)
    }

    @Test
    fun `valid feedback within Karachi bounds succeeds`() = runTest {
        val feedback = CommunityFeedback(
            id = UUID.randomUUID().toString(),
            journeyId = UUID.randomUUID().toString(),
            routeId = "route-clifton",
            latitude = 24.8150,
            longitude = 67.0320,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.COMFORTABLE),
            comment = "Smooth journey",
            createdAt = System.currentTimeMillis(),
            isSeeded = false
        )

        val result = useCase(feedback)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invalid coordinates outside Karachi are rejected by usecase`() = runTest {
        val invalidFeedback = CommunityFeedback(
            id = UUID.randomUUID().toString(),
            journeyId = UUID.randomUUID().toString(),
            routeId = null,
            latitude = 33.6844, // Islamabad
            longitude = 73.0479,
            safetyRating = CommunitySafetyRating.UNSAFE,
            contextualTags = listOf(CommunitySafetyContextTag.POOR_LIGHTING),
            comment = null,
            createdAt = System.currentTimeMillis(),
            isSeeded = false
        )

        val result = useCase(invalidFeedback)
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}
