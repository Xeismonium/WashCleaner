package com.xeismonium.washcleaner

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xeismonium.washcleaner.ui.components.navigation.DrawerContent
import com.xeismonium.washcleaner.ui.navigation.NavGraph
import com.xeismonium.washcleaner.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun WashCleanerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentRoute = currentRoute,
                onItemClick = { item ->
                    scope.launch {
                        drawerState.close()
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // popUpTo(Screen.Dashboard.route) {
                                //     saveState = true
                                // }
                                // Use simple navigation for now to match requirement
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController,
                onOpenDrawer = {
                    scope.launch { drawerState.open() }
                }
            )
        }
    }
}
