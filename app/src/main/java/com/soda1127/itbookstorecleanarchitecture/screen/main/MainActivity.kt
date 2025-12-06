package com.soda1127.itbookstorecleanarchitecture.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.soda1127.itbookstorecleanarchitecture.navigation.BottomNavigationBar
import com.soda1127.itbookstorecleanarchitecture.navigation.MainNavHost
import com.soda1127.itbookstorecleanarchitecture.screen.util.CustomSnackBarView
import com.soda1127.itbookstorecleanarchitecture.ui.theme.ItBookStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ItBookStoreTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val snackState = remember { SnackbarHostState() }
    // State from ViewModel
    val bottomNavItems = viewModel.bottomNavItems

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val showBottomBar =
                bottomNavItems.any { it.route.route == currentDestination?.route }

            if (showBottomBar) {
                BottomNavigationBar(
                    bottomNavItems = bottomNavItems,
                    navController = navController,
                    currentDestination = currentDestination,
                    onTabSelected = { index -> viewModel.onTabSelected(index) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                modifier =
                    Modifier.padding(16.dp),
                hostState = snackState,
            ) { snackData ->
                CustomSnackBarView(snackData.visuals.message)
            }
        }
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            innerPadding = innerPadding,
            snackState = snackState
        )
    }
}
