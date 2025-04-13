package com.doubletapp_hw.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doubletapp_hw.HabitApplication
import com.doubletapp_hw.R
import com.doubletapp_hw.enums.SortingType
import com.doubletapp_hw.screens.NavDrawer
import com.doubletapp_hw.screens.Routes
import com.doubletapp_hw.viewModels.HabitListViewModel
import com.doubletapp_hw.viewModels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val application = LocalContext.current.applicationContext as HabitApplication
    val viewModelFactory = ViewModelFactory(application.habitRepository)
    val habitListViewModel: HabitListViewModel = viewModel(factory = viewModelFactory)

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    NavDrawer(
        screen = Routes.Home,
        actions = {
            IconButton(
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }
        }
    ) {
        HabitPager()

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                FilterAndSearchFragment(
                    onApply = { query: String, option: SortingType, isA: Boolean ->
                        habitListViewModel.applyFilters(query, option, isA)
                        showBottomSheet = false
                    })
            }
        }
    }
}