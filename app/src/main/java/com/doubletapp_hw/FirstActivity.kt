package com.doubletapp_hw

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.doubletapp_hw.ui.theme.Dobletapp_hwTheme
import com.doubletapp_hw.screens.HabitEditScreen
import com.doubletapp_hw.screens.HabitsPagerScreen
import com.doubletapp_hw.screens.InfoScreen
import com.doubletapp_hw.screens.Routes
import kotlinx.coroutines.launch

class FirstActivity : ComponentActivity() {
    private val viewModel by viewModels<HabitListViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dobletapp_hwTheme {
                Surface(
                   // modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppNavigation(viewModel: HabitListViewModel) {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        var isNavEnabled by remember { mutableStateOf(true) }

        val items = listOf(
            Icons.Default.Home to stringResource(R.string.home) to Routes.Home,
            Icons.Default.Info to stringResource(R.string.info) to Routes.Info
        )
        val selectedItem = remember { mutableStateOf(items[0]) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                if (true){
                    ModalDrawerSheet {
                        Spacer(Modifier.height(12.dp))
                        items.forEach { item ->
                            NavigationDrawerItem(
                                icon = { Icon(item.first.first, contentDescription = null) },
                                label = { Text(item.first.second) },
                                selected = item == selectedItem.value,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    selectedItem.value = item
                                    navController.navigate(item.second.route){
                                        popUpTo(0)
                                    }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }

            },
            content = {
                Scaffold(
                    topBar = {
                        if (isNavEnabled) {
                            TopAppBar(
                                title = {
                                    Text(
                                        selectedItem.value.first.second,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = "Open Navigation Drawer"
                                        )
                                    }
                                }
                            )
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = Routes.Home.route) {
                        composable(Routes.Home.route) {
                            isNavEnabled = true

                            HabitsPagerScreen(viewModel, navController)
                        }
                        composable(Routes.Info.route) {
                            isNavEnabled = true
                            scope.launch { drawerState.close() }
                            InfoScreen(navController)
                        }
                        composable(
                            "${Routes.HabitEdit.route}/{habitId}", // Маршрут с параметром "habitId"
                            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
                        ) {
                            backStackEntry ->
                            val habitId = backStackEntry.arguments?.getString("habitId")
                            isNavEnabled = false
                            HabitEditScreen(habitId = habitId!!, viewModel, navController)
                        }
                    }
                }
            }
        )
    }
}
