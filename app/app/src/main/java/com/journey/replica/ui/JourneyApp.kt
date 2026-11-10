package com.journey.replica.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Timeline : Screen("timeline", "Timeline", Icons.Default.Home)
    data object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    data object Map : Screen("map", "Map", Icons.Default.Map)
    data object Reminders : Screen("reminders", "Reminders", Icons.Default.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyApp(vm: MainViewModel) {
    val nav = rememberNavController()
    val items = listOf(Screen.Timeline, Screen.Calendar, Screen.Map, Screen.Reminders)
    Scaffold(
        bottomBar = {
            val current = nav.currentBackStackEntryAsState().value?.destination?.route
            if (items.any { it.route == current }) {
                NavigationBar {
                    items.forEach { s ->
                        NavigationBarItem(
                            selected = current == s.route,
                            onClick = { nav.navigate(s.route) { popUpTo(Screen.Timeline.route); launchSingleTop = true } },
                            icon = { Icon(s.icon, contentDescription = s.label) },
                            label = { Text(s.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val current = nav.currentBackStackEntryAsState().value?.destination?.route
            if (current == Screen.Timeline.route || current == Screen.Calendar.route) {
                FloatingActionButton(onClick = { nav.navigate("editor/new") }) {
                    Icon(Icons.Default.Add, contentDescription = "New entry")
                }
            }
        }
    ) { pad ->
        NavHost(navController = nav, startDestination = Screen.Timeline.route, modifier = Modifier.padding(pad)) {
            composable(Screen.Timeline.route) { TimelineScreen(vm, onOpen = { nav.navigate("editor/${it}") }) }
            composable(Screen.Calendar.route) { CalendarScreen(vm, onOpen = { nav.navigate("editor/${it}") }) }
            composable(Screen.Map.route) { MapScreen(vm, onOpen = { nav.navigate("editor/${it}") }) }
            composable(Screen.Reminders.route) { RemindersScreen(vm) }
            composable("editor/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStack ->
                val id = backStack.arguments?.getString("id") ?: "new"
                EditorScreen(vm, entryId = if (id == "new") null else id, onBack = { nav.popBackStack() })
            }
        }
    }
}
