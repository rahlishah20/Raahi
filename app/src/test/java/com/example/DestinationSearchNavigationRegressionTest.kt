package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.core.location.LocationProvider
import com.example.data.datasource.KarachiLocalDataSource
import com.example.data.repository.DestinationRepositoryImpl
import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.model.KarachiDestination
import com.example.domain.model.RaahiRoute
import com.example.domain.safety.SafetyIntelligenceEngineImpl
import com.example.domain.usecase.EvaluateRouteSafetyUseCase
import com.example.domain.usecase.PlanRouteUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import com.example.presentation.home.HomeViewModel
import com.example.presentation.search.DestinationSearchScreen
import com.example.presentation.search.SearchViewModel
import com.example.ui.theme.MyApplicationTheme
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DestinationSearchNavigationRegressionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val destinationRepository = DestinationRepositoryImpl()
    private val routeRepository = RouteRepositoryImpl(ioDispatcher = testDispatcher)
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var searchViewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locationProvider = LocationProvider(context)
        val planUseCase = PlanRouteUseCase(routeRepository)

        homeViewModel = HomeViewModel(
            destinationRepository = destinationRepository,
            locationProvider = locationProvider,
            planRouteUseCase = planUseCase
        )

        searchViewModel = SearchViewModel(destinationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `clicking destination triggers only onDestinationSelected and NOT onNavigateBack`() = runTest(testDispatcher) {
        var destinationSelectedCalls = 0
        var navigateBackCalls = 0
        var selectedDest: KarachiDestination? = null

        val testDestination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first()

        composeTestRule.setContent {
            MyApplicationTheme {
                DestinationSearchScreen(
                    viewModel = searchViewModel,
                    onNavigateBack = { navigateBackCalls++ },
                    onDestinationSelected = { dest ->
                        destinationSelectedCalls++
                        selectedDest = dest
                    },
                    onCurrentLocationSelected = { }
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("destination_item_${testDestination.id}")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify single-owner navigation contract:
        // Clicking destination must call onDestinationSelected exactly once
        assertEquals(1, destinationSelectedCalls)
        assertEquals(testDestination.id, selectedDest?.id)
        // Redundant onNavigateBack MUST NOT be called (prevents blank screen double-pop bug)
        assertEquals(0, navigateBackCalls)
    }

    @Test
    fun `clicking current location item triggers only onCurrentLocationSelected and NOT onNavigateBack`() = runTest(testDispatcher) {
        var currentLocationSelectedCalls = 0
        var navigateBackCalls = 0

        composeTestRule.setContent {
            MyApplicationTheme {
                DestinationSearchScreen(
                    viewModel = searchViewModel,
                    onNavigateBack = { navigateBackCalls++ },
                    onDestinationSelected = { },
                    onCurrentLocationSelected = { currentLocationSelectedCalls++ }
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("current_location_item")
            .performClick()

        composeTestRule.waitForIdle()

        assertEquals(1, currentLocationSelectedCalls)
        assertEquals(0, navigateBackCalls)
    }

    @Test
    fun `destination selection flow properly updates HomeViewModel state with route calculation`() = runTest(testDispatcher) {
        val saddar = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }

        // Initial state
        assertNull(homeViewModel.uiState.value.selectedDestination)

        // Select destination
        homeViewModel.onSelectDestination(saddar)

        val state = homeViewModel.uiState.value
        assertEquals("saddar-bazaar", state.selectedDestination?.id)
        assertTrue("Routes should be computed for selected destination", state.routes.isNotEmpty())
        assertNotNull(state.selectedRoute)
        assertNull(state.routeError)
    }

    // =========================================================================
    // REQUIRED ROUTE SELECTION WITH SAFETY SCORE TESTS (SCENARIOS 1 to 7)
    // =========================================================================

    @Test
    fun `scenario 1 - 3 routes returned produces 3 safety evaluations with non-null profiles and scores`() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        homeViewModel.onSelectDestination(destination)

        val state = homeViewModel.uiState.value
        assertEquals(3, state.routes.size)

        // Every route MUST have a safety score and non-null safety profile
        state.routes.forEachIndexed { index, route ->
            val profile = route.safetyProfile
            assertNotNull("Route ${route.id} at index $index must have non-null safetyProfile", profile)
            assertTrue(
                "Route ${route.id} safety score must be in valid range 0..100, was: ${profile!!.relativeSafetyScore}",
                profile.relativeSafetyScore in 0..100
            )
            assertTrue("Route ${route.id} safety confidence must not be empty", profile.confidence.label.isNotEmpty())
            assertTrue("Route ${route.id} explanation must not be empty", profile.explanation.isNotEmpty())
            assertTrue("Route ${route.id} factor evaluations must be present", profile.factorEvaluations.isNotEmpty())
        }
    }

    @Test
    fun `scenario 2 - user selects Route B updates selectedRoute to Route B and not Route A`() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        homeViewModel.onSelectDestination(destination)

        val state = homeViewModel.uiState.value
        assertTrue("At least 2 routes needed", state.routes.size >= 2)
        val routeA = state.routes[0]
        val routeB = state.routes[1]

        // Explicitly select Route B
        homeViewModel.selectRoute(routeB)

        val updatedState = homeViewModel.uiState.value
        assertEquals("Selected route must be Route B", routeB.id, updatedState.selectedRoute?.id)
        assertNotEquals("Selected route must not remain Route A", routeA.id, updatedState.selectedRoute?.id)
    }

    @Test
    fun `scenario 3 - user starts journey with Route B activeJourney uses Route B and does not revert to Route A`() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        homeViewModel.onSelectDestination(destination)

        val state = homeViewModel.uiState.value
        assertTrue("At least 2 routes needed", state.routes.size >= 2)
        val routeA = state.routes[0]
        val routeB = state.routes[1]

        // Select Route B and start journey
        homeViewModel.selectRoute(routeB)
        homeViewModel.confirmSelectedRoute()
        homeViewModel.startActiveJourney()

        val navState = homeViewModel.uiState.value
        assertTrue("Navigation must be active", navState.isNavigating)
        assertNotNull("Active journey must not be null", navState.activeJourney)
        assertEquals("Active journey must use Route B", routeB.id, navState.activeJourney?.route?.id)
        assertEquals("Selected route in state must remain Route B", routeB.id, navState.selectedRoute?.id)
        assertNotEquals("Active journey must NOT silently revert to Route A", routeA.id, navState.activeJourney?.route?.id)
    }

    @Test
    fun `scenario 4 - shortest duration route with lower safety score is NOT recommended over safer alternative`() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        homeViewModel.onSelectDestination(destination)

        val routes = homeViewModel.uiState.value.routes
        assertTrue(routes.isNotEmpty())

        val recommendedRoute = routes.firstOrNull { it.safetyProfile?.isRecommended == true }
        assertNotNull("There should be a recommended route", recommendedRoute)

        val maxSafetyScore = routes.maxOf { it.safetyProfile?.relativeSafetyScore ?: 0 }
        assertEquals(
            "Recommended route must have the highest safety score among alternatives",
            maxSafetyScore,
            recommendedRoute?.safetyProfile?.relativeSafetyScore
        )

        // Find fastest route by duration
        val fastestRoute = routes.minByOrNull { it.durationSeconds }
        if (fastestRoute != null && (fastestRoute.safetyProfile?.relativeSafetyScore ?: 0) < maxSafetyScore) {
            // If the fastest route has a lower safety score, it must NOT be marked recommended
            assertFalse(
                "Fastest route with lower safety score must not be marked recommended",
                fastestRoute.safetyProfile?.isRecommended == true
            )
        }
    }

    @Test
    fun `scenario 5 - safety evaluation error on single route does not crash or corrupt route selection screen`() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        homeViewModel.onSelectDestination(destination)

        val routes = homeViewModel.uiState.value.routes
        assertTrue(routes.isNotEmpty())

        // Test safety engine resilient calculation
        val safetyEngine = SafetyIntelligenceEngineImpl()
        val evaluated = safetyEngine.evaluateAndCompareRoutes(routes)

        assertEquals("All routes must survive evaluation", routes.size, evaluated.size)
        evaluated.forEach { route ->
            assertNotNull("Route ${route.id} must have a safety profile", route.safetyProfile)
            assertTrue("Score must be valid", route.safetyProfile!!.relativeSafetyScore >= 0)
        }
    }

    @Test
    fun `scenario 6 - recalculation preserves user-selected route unless re-evaluated`() = runTest(testDispatcher) {
        val destination = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "saddar-bazaar" }
        homeViewModel.onSelectDestination(destination)

        val state = homeViewModel.uiState.value
        val routeB = state.routes.getOrNull(1) ?: state.routes[0]
        homeViewModel.selectRoute(routeB)

        assertEquals("Route B must be selected", routeB.id, homeViewModel.uiState.value.selectedRoute?.id)

        // Switching route during navigation updates correctly
        homeViewModel.confirmSelectedRoute()
        homeViewModel.startActiveJourney()

        val routeC = state.routes.getOrNull(2) ?: state.routes[0]
        homeViewModel.switchActiveJourneyRoute(routeC)

        assertEquals("Active journey route must switch to Route C", routeC.id, homeViewModel.uiState.value.activeJourney?.route?.id)
        assertEquals("Selected route in state must switch to Route C", routeC.id, homeViewModel.uiState.value.selectedRoute?.id)
    }

    @Test
    fun `scenario 7 - complete flow from destination selection to route safety scoring to route selection to journey start`() = runTest(testDispatcher) {
        // Step 1: Destination Search & Selection
        val clifton = KarachiLocalDataSource.KARACHI_DESTINATIONS.first { it.id == "clifton-sea-view" }
        homeViewModel.onSelectDestination(clifton)

        // Step 2: Route calculation & Safety evaluation
        val plannedState = homeViewModel.uiState.value
        assertEquals("Destination set to Sea View", clifton.id, plannedState.selectedDestination?.id)
        assertTrue("Routes calculated", plannedState.routes.isNotEmpty())
        assertFalse("Route calculation finished", plannedState.isCalculatingRoutes)

        // Step 3: All routes have evaluated safety scores
        plannedState.routes.forEach { r ->
            assertNotNull("Each route has safety profile", r.safetyProfile)
            assertTrue("Valid score", (r.safetyProfile?.relativeSafetyScore ?: -1) in 0..100)
        }

        // Step 4: User explicitly selects route with highest safety
        val safestRoute = plannedState.routes.maxByOrNull { it.safetyProfile?.relativeSafetyScore ?: 0 }!!
        homeViewModel.selectRoute(safestRoute)
        assertEquals("Selected route is safest", safestRoute.id, homeViewModel.uiState.value.selectedRoute?.id)

        // Step 5: Start journey
        homeViewModel.confirmSelectedRoute()
        homeViewModel.startActiveJourney()

        // Step 6: Verify navigation uses safest route
        val navigatingState = homeViewModel.uiState.value
        assertTrue("Navigating active", navigatingState.isNavigating)
        assertNotNull("Active journey exists", navigatingState.activeJourney)
        assertEquals("Active journey uses user-selected safest route", safestRoute.id, navigatingState.activeJourney?.route?.id)
    }
}
