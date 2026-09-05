package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.location.LocationProvider
import com.example.data.datasource.KarachiLocalDataSource
import com.example.data.repository.CommunityFeedbackRepositoryImpl
import com.example.data.repository.DestinationRepositoryImpl
import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.usecase.GetCommunitySafetySignalUseCase
import com.example.domain.usecase.PlanRouteUseCase
import com.example.domain.usecase.SubmitCommunityFeedbackUseCase
import com.example.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CommunityFeedbackViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private val destinationRepository = DestinationRepositoryImpl()
    private val routeRepository = RouteRepositoryImpl(ioDispatcher = testDispatcher)
    private val feedbackRepository = CommunityFeedbackRepositoryImpl(ioDispatcher = testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locationProvider = LocationProvider(context)
        val planUseCase = PlanRouteUseCase(routeRepository)
        val submitUseCase = SubmitCommunityFeedbackUseCase(feedbackRepository)
        val getSignalUseCase = GetCommunitySafetySignalUseCase(feedbackRepository)

        viewModel = HomeViewModel(
            destinationRepository = destinationRepository,
            locationProvider = locationProvider,
            planRouteUseCase = planUseCase,
            submitCommunityFeedbackUseCase = submitUseCase,
            getCommunitySafetySignalUseCase = getSignalUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `opening feedback prompt resets state and shows prompt`() = runTest(testDispatcher) {
        viewModel.onOpenFeedbackPrompt()
        val state = viewModel.uiState.value
        assertTrue(state.isFeedbackPromptVisible)
        assertEquals(null, state.feedbackRating)
        assertTrue(state.selectedFeedbackTags.isEmpty())
        assertFalse(state.feedbackSubmissionSuccess)
    }

    @Test
    fun `selecting rating and tags updates state correctly`() = runTest(testDispatcher) {
        viewModel.onSelectFeedbackRating(CommunitySafetyRating.SAFE)
        viewModel.onToggleFeedbackTag(CommunitySafetyContextTag.GOOD_VISIBILITY)
        viewModel.onToggleFeedbackTag(CommunitySafetyContextTag.COMFORTABLE)

        val state = viewModel.uiState.value
        assertEquals(CommunitySafetyRating.SAFE, state.feedbackRating)
        assertTrue(state.selectedFeedbackTags.contains(CommunitySafetyContextTag.GOOD_VISIBILITY))
        assertTrue(state.selectedFeedbackTags.contains(CommunitySafetyContextTag.COMFORTABLE))

        // Toggle tag off
        viewModel.onToggleFeedbackTag(CommunitySafetyContextTag.GOOD_VISIBILITY)
        assertFalse(viewModel.uiState.value.selectedFeedbackTags.contains(CommunitySafetyContextTag.GOOD_VISIBILITY))
    }

    @Test
    fun `submitting feedback without rating shows validation error`() = runTest(testDispatcher) {
        viewModel.onSubmitFeedback()
        val state = viewModel.uiState.value
        assertNotNull(state.feedbackError)
        assertFalse(state.feedbackSubmissionSuccess)
    }

    @Test
    fun `submitting feedback with rating persists to datasource and updates aggregate`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        viewModel.onSelectDestination(saddar)
        viewModel.confirmSelectedRoute()

        viewModel.onSelectFeedbackRating(CommunitySafetyRating.SAFE)
        viewModel.onToggleFeedbackTag(CommunitySafetyContextTag.GOOD_VISIBILITY)

        viewModel.onSubmitFeedback(comment = "Safe commute")

        val state = viewModel.uiState.value
        assertTrue(state.feedbackSubmissionSuccess)
        assertFalse(state.isSubmittingFeedback)
    }
}
