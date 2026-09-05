package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.location.LocationProvider
import com.example.data.datasource.KarachiLocalDataSource
import com.example.data.repository.DestinationRepositoryImpl
import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.usecase.EvaluateRouteSafetyUseCase
import com.example.domain.usecase.PlanRouteUseCase
import com.example.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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
class SafetyViewModelIntegrationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private val destinationRepository = DestinationRepositoryImpl()
    private val routeRepository = RouteRepositoryImpl(ioDispatcher = testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locationProvider = LocationProvider(context)
        val planUseCase = PlanRouteUseCase(routeRepository)
        val evaluateSafetyUseCase = EvaluateRouteSafetyUseCase()

        viewModel = HomeViewModel(
            destinationRepository = destinationRepository,
            locationProvider = locationProvider,
            planRouteUseCase = planUseCase,
            evaluateRouteSafetyUseCase = evaluateSafetyUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `planning route attaches safety profile and defaults to recommended route`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }

        viewModel.onSelectDestination(saddar)

        val state = viewModel.uiState.value
        assertTrue(state.routes.isNotEmpty())

        // Every computed route must have a valid safety profile
        state.routes.forEach { route ->
            val profile = route.safetyProfile
            assertNotNull("Each route must have an attached safety profile", profile)
            assertTrue("Score in 0..100 range", profile!!.relativeSafetyScore in 0..100)
            assertTrue("Must contain factor evaluations", profile.factorEvaluations.isNotEmpty())
        }

        // Default selected route should be the recommended route
        assertNotNull(state.selectedRoute)
        assertEquals(true, state.selectedRoute?.safetyProfile?.isRecommended)
    }

    @Test
    fun `switching routes preserves individual safety profile evaluation`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }

        viewModel.onSelectDestination(saddar)

        val state = viewModel.uiState.value
        assertTrue(state.routes.size >= 2)

        val firstRoute = state.routes[0]
        val secondRoute = state.routes[1]

        viewModel.selectRoute(secondRoute)
        assertEquals(secondRoute.id, viewModel.uiState.value.selectedRoute?.id)
        assertNotNull(viewModel.uiState.value.selectedRoute?.safetyProfile)

        viewModel.selectRoute(firstRoute)
        assertEquals(firstRoute.id, viewModel.uiState.value.selectedRoute?.id)
        assertNotNull(viewModel.uiState.value.selectedRoute?.safetyProfile)
    }
}
