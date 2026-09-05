package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.location.LocationProvider
import com.example.data.datasource.KarachiDynamicConditionDataSource
import com.example.data.datasource.KarachiLocalDataSource
import com.example.data.repository.DemoEventRepositoryImpl
import com.example.data.repository.DestinationRepositoryImpl
import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.model.DemoEvent
import com.example.domain.model.DemoEventType
import com.example.domain.model.RecalculationState
import com.example.domain.model.SafetyFactorType
import com.example.domain.usecase.ApplyDemoEventUseCase
import com.example.domain.usecase.EvaluateRouteSafetyUseCase
import com.example.domain.usecase.PlanRouteUseCase
import com.example.domain.usecase.RecalculateSafetyUseCase
import com.example.domain.usecase.ResetDemoEventsUseCase
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
class DynamicRecalculationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private val destinationRepository = DestinationRepositoryImpl()
    private val routeRepository = RouteRepositoryImpl(ioDispatcher = testDispatcher)
    private val demoEventRepository = DemoEventRepositoryImpl()
    private lateinit var planUseCase: PlanRouteUseCase
    private lateinit var recalculateUseCase: RecalculateSafetyUseCase
    private lateinit var applyEventUseCase: ApplyDemoEventUseCase
    private lateinit var resetEventsUseCase: ResetDemoEventsUseCase

    @Before
    fun setup() {
        KarachiDynamicConditionDataSource.clearAllEvents()
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locationProvider = LocationProvider(context)
        planUseCase = PlanRouteUseCase(routeRepository)
        val evaluateSafetyUseCase = EvaluateRouteSafetyUseCase()
        recalculateUseCase = RecalculateSafetyUseCase(
            demoEventRepository = demoEventRepository,
            ioDispatcher = testDispatcher
        )
        applyEventUseCase = ApplyDemoEventUseCase(
            demoEventRepository = demoEventRepository,
            recalculateSafetyUseCase = recalculateUseCase,
            dispatcher = testDispatcher
        )
        resetEventsUseCase = ResetDemoEventsUseCase(
            demoEventRepository = demoEventRepository,
            recalculateSafetyUseCase = recalculateUseCase,
            dispatcher = testDispatcher
        )

        viewModel = HomeViewModel(
            destinationRepository = destinationRepository,
            locationProvider = locationProvider,
            planRouteUseCase = planUseCase,
            evaluateRouteSafetyUseCase = evaluateSafetyUseCase,
            demoEventRepository = demoEventRepository,
            recalculateSafetyUseCase = recalculateUseCase,
            applyDemoEventUseCase = applyEventUseCase,
            resetDemoEventsUseCase = resetEventsUseCase
        )
    }

    @After
    fun tearDown() {
        KarachiDynamicConditionDataSource.clearAllEvents()
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialDynamicStateIsIdle() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertEquals(RecalculationState.IDLE, state.recalculationState)
        assertTrue(state.activeDemoEvents.isEmpty())
        assertNull(state.recalculationNotice)
    }

    @Test
    fun testApplyDemoEventUseCaseDirectly() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        val origin = com.example.domain.model.LatLngPoint(24.8607, 67.0011)
        val dest = com.example.domain.model.LatLngPoint(destination.latitude, destination.longitude)
        val routesResult = planUseCase(
            origin = origin,
            destination = dest,
            originName = "Current Location",
            destinationName = destination.name
        )
        assertTrue(routesResult.isSuccess)
        val routes = routesResult.getOrThrow()
        assertTrue(routes.isNotEmpty())

        val event = DemoEvent(
            id = "test_drop_1",
            type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
            targetRouteId = routes.first().id,
            targetFactorType = SafetyFactorType.BUSINESS_ACTIVITY,
            valueMultiplier = 0.20,
            description = "Simulated commercial activity drop"
        )

        val result = applyEventUseCase(event, routes)
        assertTrue(result.isSuccess)
        val recalc = result.getOrThrow()
        assertEquals(1, recalc.activeEventsCount)
        assertNotNull(recalc.summaryNotice)

        val resetResult = resetEventsUseCase(recalc.recalculatedRoutes)
        assertTrue(resetResult.isSuccess)
        assertEquals(0, resetResult.getOrThrow().activeEventsCount)
    }

    @Test
    fun testDismissRecalculationNotice() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        viewModel.onSelectDestination(destination)

        viewModel.recalculateRouteSafety()
        viewModel.dismissRecalculationNotice()
        assertNull(viewModel.uiState.value.recalculationNotice)
    }
}
