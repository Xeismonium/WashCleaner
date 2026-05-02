package com.xeismonium.washcleaner.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.navigation.NavGraph
import com.xeismonium.washcleaner.navigation.Route
import kotlinx.coroutines.launch

private data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val allowedRoles: List<UserRole>
)

private val bottomNavItems = listOf(
    BottomNavItem(Route.Orders.route, "Orders", Icons.Default.List, listOf(UserRole.OWNER, UserRole.STAFF)),
    BottomNavItem(Route.Customers.route, "Customers", Icons.Default.Person, listOf(UserRole.OWNER, UserRole.STAFF)),
    BottomNavItem(Route.Reports.route, "Reports", Icons.Default.Info, listOf(UserRole.OWNER)),
    BottomNavItem(Route.Settings.route, "Settings", Icons.Default.Settings, listOf(UserRole.OWNER, UserRole.STAFF))
)

@Composable
fun MainScreen(
    userRole: UserRole,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    MainContent(
        userRole = userRole,
        currentRoute = currentDestination?.route,
        snackbarHostState = snackbarHostState,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            userRole = userRole,
            onAccessDenied = {
                scope.launch {
                    snackbarHostState.showSnackbar("Access Denied")
                }
            },
            startDestination = Route.Splash.route,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MainContent(
    userRole: UserRole,
    currentRoute: String?,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val showBottomBar = currentRoute != null && 
                        currentRoute != Route.Splash.route && 
                        currentRoute != Route.Login.route &&
                        currentRoute != Route.Register.route &&
                        currentRoute != Route.ForgotPassword.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.filter { userRole in it.allowedRoles }.forEach { item ->
                        val selected = currentRoute == item.route || currentRoute?.startsWith(item.route + "/") == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = { onNavigate(item.route) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainContent(
        userRole = UserRole.OWNER,
        currentRoute = Route.Orders.route,
        onNavigate = {}
    ) {
        // Preview content
    }
}