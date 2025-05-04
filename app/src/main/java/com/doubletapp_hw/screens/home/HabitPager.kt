package com.doubletapp_hw.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.doubletapp_hw.HabitApplication
import com.doubletapp_hw.LocalNavController
import com.doubletapp_hw.R
import com.doubletapp_hw.enums.HabitType
import com.doubletapp_hw.screens.Routes
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HabitPager() {
    val app = LocalContext.current.applicationContext as HabitApplication
    val navController = LocalNavController.current

    val coroutineScope = rememberCoroutineScope()
    val pages = HabitType.entries
    val pagerState = rememberPagerState { pages.size }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Routes.HabitEdit(""))
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
            }
        }) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Вкладки для переключения между страницами
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp)
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(selected = pagerState.currentPage == index, onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }, text = {
                        Text(text = stringResource(id = page.labelResId))
                    })
                }
            }

            HorizontalPager(
                state = pagerState, modifier = Modifier.weight(1f)
            ) { pageIndex ->
                // Определение типа привычек на основе текущей позиции в пейджере
                val habitType = pages[pageIndex]
                HabitListByType(habitType)
            }
        }
    }
}