package com.espressodev.gptmap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.espressodev.gptmap.feature.favourite.favouriteRoute
import com.espressodev.gptmap.feature.favourite.favouriteScreen
import com.espressodev.gptmap.feature.forgot_password.forgotPasswordRoute
import com.espressodev.gptmap.feature.forgot_password.forgotPasswordScreen
import com.espressodev.gptmap.feature.image_analyses.imageAnalysesRoute
import com.espressodev.gptmap.feature.image_analyses.imageAnalysesScreen
import com.espressodev.gptmap.feature.image_analysis.imageAnalysisScreen
import com.espressodev.gptmap.feature.image_analysis.navigateToImageAnalysis
import com.espressodev.gptmap.feature.login.loginRoute
import com.espressodev.gptmap.feature.login.loginScreen
import com.espressodev.gptmap.feature.map.mapRoute
import com.espressodev.gptmap.feature.map.mapScreen
import com.espressodev.gptmap.feature.map.navigateToMap
import com.espressodev.gptmap.feature.register.registerRoute
import com.espressodev.gptmap.feature.register.registerScreen
import com.espressodev.gptmap.feature.screenshot.screenshotRoute
import com.espressodev.gptmap.feature.screenshot.screenshotScreen
import com.espressodev.gptmap.feature.street_view.navigateToStreetView
import com.espressodev.gptmap.feature.street_view.streetViewScreen

@Composable
fun GmNavHost(
    appState: GmAppState,
    modifier: Modifier = Modifier,
    startDestination: String = loginRoute
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        mapScreen(
            navigateToStreetView = { latitude, longitude ->
                navController.navigateToStreetView(latitude, longitude)
            },
            navigateToFavourite = { appState.navigate(favouriteRoute) },
            navigateToScreenshot = { appState.navigate(screenshotRoute) },
            navigateToImageAnalyses = { appState.navigate(imageAnalysesRoute) }
        )
        loginScreen(
            navigateToMap = { navController.navigateToMap() },
            navigateToRegister = { appState.navigate(registerRoute) },
            navigateToForgotPassword = { appState.navigate(forgotPasswordRoute) }
        )
        registerScreen(
            navigateToLogin = { appState.clearAndNavigate(loginRoute) },
            navigateToMap = { appState.clearAndNavigate(mapRoute) }
        )
        forgotPasswordScreen(navigateToLogin = { appState.clearAndNavigate(loginRoute) })
        streetViewScreen(popUp = appState::popUp)
        favouriteScreen(
            popUp = appState::popUp,
            navigateToMap = { favouriteId ->
                appState.navController.navigateToMap(favouriteId)
            }
        )
        screenshotScreen(
            popUp = appState::popUp,
            navigateToImageAnalysis = {
                navController.navigateToImageAnalysis {
                    launchSingleTop = true
                    popUpTo(mapRoute) { inclusive = false }
                }
            }
        )
        imageAnalysisScreen(popUp = appState::popUp)
        imageAnalysesScreen(popUp = appState::popUp)
    }
}
