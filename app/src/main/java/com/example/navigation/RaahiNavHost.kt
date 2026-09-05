package com.example.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.repository.DestinationRepository
import com.example.domain.repository.RouteRepository
import com.example.presentation.components.RaahiNavTab
import com.example.presentation.demo.EnvironmentControlScreen
import com.example.presentation.history.HistoryScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.profile.ProfileScreen
import com.example.presentation.search.DestinationSearchScreen
import com.example.presentation.search.SearchViewModel

@Composable
fun RaahiNavHost(
    destinationRepository: DestinationRepository,
    modifier: Modifier = Modifier,
    routeRepository: RouteRepository = RouteRepositoryImpl(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            destinationRepository = destinationRepository,
            context = context,
            routeRepository = routeRepository
        )
    )

    NavHost(
        navController = navController,
        startDestination = RaahiDestination.Home.route,
        modifier = modifier
    ) {
        composable(
            route = RaahiDestination.Home.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSearch = {
                    navController.navigate(RaahiDestination.Search.route)
                },
                onNavigateToHistory = {
                    navController.navigate(RaahiDestination.History.route)
                },
                onNavigateToProfile = {
                    navController.navigate(RaahiDestination.Profile.route)
                },
                onNavigateToEnvironmentControl = {
                    navController.navigate(RaahiDestination.EnvironmentControl.route)
                }
            )
        }

        composable(
            route = RaahiDestination.Search.route,
            enterTransition = { slideInVertically(initialOffsetY = { it / 4 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { it / 4 }) + fadeOut() }
        ) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.Factory(destinationRepository)
            )

            DestinationSearchScreen(
                viewModel = searchViewModel,
                onNavigateBack = {
                    if (navController.currentDestination?.route == RaahiDestination.Search.route) {
                        navController.popBackStack()
                    }
                },
                onDestinationSelected = { destination ->
                    homeViewModel.onSelectDestination(destination)
                    if (navController.currentDestination?.route == RaahiDestination.Search.route) {
                        navController.popBackStack()
                    }
                },
                onCurrentLocationSelected = {
                    homeViewModel.fetchCurrentLocation()
                    if (navController.currentDestination?.route == RaahiDestination.Search.route) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = RaahiDestination.History.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            val state by homeViewModel.uiState.collectAsStateWithLifecycle()
            HistoryScreen(
                journeyHistory = state.journeyHistory,
                selectedNavTab = RaahiNavTab.HISTORY,
                onNavTabSelected = { tab ->
                    homeViewModel.onNavTabSelected(tab)
                    when (tab) {
                        RaahiNavTab.EXPLORE -> navController.navigate(RaahiDestination.Home.route) {
                            popUpTo(RaahiDestination.Home.route) { inclusive = false }
                        }
                        RaahiNavTab.HISTORY -> {}
                        RaahiNavTab.PROFILE -> navController.navigate(RaahiDestination.Profile.route) {
                            popUpTo(RaahiDestination.Home.route) { inclusive = false }
                        }
                    }
                },
                onBackClick = {
                    if (navController.currentDestination?.route == RaahiDestination.History.route) {
                        navController.popBackStack()
                    }
                },
                onPlanNewJourney = {
                    homeViewModel.onNavTabSelected(RaahiNavTab.EXPLORE)
                    navController.navigate(RaahiDestination.Home.route) {
                        popUpTo(RaahiDestination.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = RaahiDestination.Profile.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            val state by homeViewModel.uiState.collectAsStateWithLifecycle()
            ProfileScreen(
                profileName = state.profileName,
                profilePhotoPath = state.profilePhotoPath,
                onUpdateProfileName = homeViewModel::updateProfileName,
                onUpdateProfilePhoto = homeViewModel::updateProfilePhoto,
                selectedNavTab = RaahiNavTab.PROFILE,
                onNavTabSelected = { tab ->
                    homeViewModel.onNavTabSelected(tab)
                    when (tab) {
                        RaahiNavTab.EXPLORE -> navController.navigate(RaahiDestination.Home.route) {
                            popUpTo(RaahiDestination.Home.route) { inclusive = false }
                        }
                        RaahiNavTab.HISTORY -> navController.navigate(RaahiDestination.History.route) {
                            popUpTo(RaahiDestination.Home.route) { inclusive = false }
                        }
                        RaahiNavTab.PROFILE -> {}
                    }
                },
                onNavigateToEnvironmentControl = {
                    navController.navigate(RaahiDestination.EnvironmentControl.route)
                },
                onBackClick = {
                    if (navController.currentDestination?.route == RaahiDestination.Profile.route) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = RaahiDestination.EnvironmentControl.route,
            enterTransition = { slideInVertically(initialOffsetY = { it / 4 }) + fadeIn() },
            exitTransition = { slideOutVertically(targetOffsetY = { it / 4 }) + fadeOut() }
        ) {
            val state by homeViewModel.uiState.collectAsStateWithLifecycle()
            EnvironmentControlScreen(
                activeDemoEvents = state.activeDemoEvents,
                onApplyEvent = { event ->
                    homeViewModel.applyDemoEvent(event)
                },
                onApplyScenario = { events ->
                    homeViewModel.applyDemoScenario(events)
                },
                onResetSimulation = {
                    homeViewModel.resetDemoSimulation()
                },
                onBackClick = {
                    if (navController.currentDestination?.route == RaahiDestination.EnvironmentControl.route) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
