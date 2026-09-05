package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.location.LocationProvider
import com.example.data.datasource.KarachiLocalDataSource
import com.example.data.repository.DestinationRepositoryImpl
import com.example.data.repository.RouteRepositoryImpl
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomeViewModelRouteTest {

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

        viewModel = HomeViewModel(
            destinationRepository = destinationRepository,
            locationProvider = locationProvider,
            planRouteUseCase = planUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selecting destination computes routes and populates state`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }

        viewModel.onSelectDestination(saddar)

        val state = viewModel.uiState.value
        assertEquals(saddar, state.selectedDestination)
        assertTrue(state.routes.isNotEmpty())
        assertNotNull(state.selectedRoute)
        assertEquals(state.routes.first().id, state.selectedRoute?.id)
        assertNull(state.routeError)
    }

    @Test
    fun `user can select alternative route`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }

        viewModel.onSelectDestination(saddar)

        val state = viewModel.uiState.value
        assertTrue(state.routes.size >= 2)

        val altRoute = state.routes[1]
        viewModel.selectRoute(altRoute)

        val updatedState = viewModel.uiState.value
        assertEquals(altRoute.id, updatedState.selectedRoute?.id)
    }

    @Test
    fun `clearing destination resets routes and selected state`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }

        viewModel.onSelectDestination(saddar)

        assertTrue(viewModel.uiState.value.routes.isNotEmpty())

        viewModel.clearSelectedDestination()

        val state = viewModel.uiState.value
        assertNull(state.selectedDestination)
        assertTrue(state.routes.isEmpty())
        assertNull(state.selectedRoute)
    }
}
