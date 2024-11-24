package com.example.shelfshare


import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shelfshare.common.snackbar.SnackbarManager
import com.example.shelfshare.ui.screens.auth.forgot_password.ForgotPasswordScreen
import com.example.shelfshare.ui.screens.auth.login.LoginScreen
import com.example.shelfshare.ui.screens.auth.sign_up.SignUpScreen
import com.example.shelfshare.ui.screens.cart.CartScreen
import com.example.shelfshare.ui.screens.home.HomeScreen
import com.example.shelfshare.ui.screens.order_details.OrderDetailsScreen
import com.example.shelfshare.ui.screens.orders.OrdersScreen
import com.example.shelfshare.ui.screens.profile.ProfileScreen
import com.example.shelfshare.ui.screens.sell.SellScreen
import com.example.shelfshare.ui.screens.splash.SplashScreen
import com.example.shelfshare.ui.theme.ShelfShareTheme
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShelfShareApp() {
    ShelfShareTheme {
        Surface(color = MaterialTheme.colors.background) {
            val appState = rememberAppState()

            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val noBottomBarScreens = listOf(
                SPLASH_SCREEN,
                LOGIN_SCREEN,
                SIGNUP_SCREEN,
                FORGOT_PASSWORD_SCREEN
            )

            // Check if bottom bar should be shown for current route
            val shouldShowBottomBar =
                currentRoute != null && !noBottomBarScreens.contains(currentRoute)

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData, contentColor = MaterialTheme.colors.onPrimary)
                        }
                    )
                },
                scaffoldState = appState.scaffoldState,
                bottomBar = {
                    if (shouldShowBottomBar) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colors.primary)
                                .padding(bottom = 8.dp)
                        ) {
                            BottomNavigation {
                                BottomNavItem.getBtmNavs().forEach { bottomNav ->
                                    BottomNavigationItem(
                                        icon = {
                                            Icon(
                                                bottomNav.icon,
                                                contentDescription = bottomNav.label
                                            )
                                        },
                                        onClick = { appState.navigate(bottomNav.route) },
                                        selected = currentRoute == bottomNav.route,
                                        label = { Text(bottomNav.label) }
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    shelfShareGraph(appState)
                }
            }
        }
    }
}


@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
        ShelfShareAppState(scaffoldState, navController, snackbarManager, resources, coroutineScope)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@ExperimentalMaterialApi
fun NavGraphBuilder.shelfShareGraph(appState: ShelfShareAppState) {
    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
    composable(HOME_SCREEN) { HomeScreen(openScreen = { route -> appState.navigate(route) }) }

    composable(ORDERS_SCREEN) { OrdersScreen(openScreen = { route -> appState.navigate(route) }) }

    composable(
        "$ORDERS_SCREEN/{orderId}",
        arguments = listOf(
            navArgument("orderId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId")
        OrderDetailsScreen(
            orderId = orderId ?: "",
            navigateBack = { appState.popUp() }
        )
    }

    composable(SELL_SCREEN) { SellScreen(openScreen = { route -> appState.navigate(route) }) }

    composable(LOGIN_SCREEN) {
        LoginScreen(openAndPopUp = { route, popUp ->
            appState.navigateAndPopUp(
                route,
                popUp
            )
        })
    }

    composable(SIGNUP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp ->
            appState.navigateAndPopUp(
                route,
                popUp
            )
        })
    }

    composable(FORGOT_PASSWORD_SCREEN) {
        ForgotPasswordScreen(openScreen = { route ->
            appState.navigate(
                route
            )
        })
    }

    composable(PROFILE_SCREEN) {
        ProfileScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(CART_SCREEN) {
        CartScreen(openScreen = { route ->
            appState.navigate(
                route
            )
        })
    }


}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(HOME_SCREEN, icon = Icons.Default.Home, "Home")
    data object Sell : BottomNavItem(SELL_SCREEN, icon = Icons.Default.Sell, "Sell")
    data object Orders : BottomNavItem(ORDERS_SCREEN, icon = Icons.Default.ShoppingCart, "Orders")
    data object Profile : BottomNavItem(PROFILE_SCREEN, icon = Icons.Default.Person, "Profile")

    companion object {
        fun getBtmNavs(): List<BottomNavItem> {
            return mutableListOf<BottomNavItem>(
                BottomNavItem.Home,
                BottomNavItem.Sell,
                BottomNavItem.Orders,
                BottomNavItem.Profile
            )
        }
    }
}