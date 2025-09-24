package at.mcbabo.calsync.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import at.mcbabo.calsync.ui.screen.AddCalendarScreen
import at.mcbabo.calsync.ui.screen.CalendarDetailScreen
import at.mcbabo.calsync.ui.screen.CalendarListScreen
import at.mcbabo.calsync.ui.screen.SettingsScreen


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SimpleNavApp(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        animatedComposable(route = "home") {
            CalendarListScreen(
                navController = navController,
                onAddCalendar = { navController.navigate("add_calendar") }
            )
        }

        animatedComposable(
            route = "calendar/{calendarId}",
            arguments = listOf(
                navArgument("calendarId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val calendarId = backStackEntry.arguments?.getLong("calendarId") ?: 0L
            CalendarDetailScreen(
                calendarId = calendarId,
                onBack = { navController.popBackStack() }
            )
        }

        animatedComposable(route = "add_calendar") {
            AddCalendarScreen { navController.popBackStack() }
        }

        animatedComposable(route = "settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}