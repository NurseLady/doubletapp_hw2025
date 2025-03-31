package com.doubletapp_hw.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitListViewModel
import com.doubletapp_hw.HabitType
import com.doubletapp_hw.R
import kotlinx.coroutines.launch

@Composable
fun HabitsPagerScreen(viewModel: HabitListViewModel, onNavigate: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val pages = HabitType.entries.toList()
    val pagerState = rememberPagerState { pages.size }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigate("new")
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            // Вкладки для переключения между страницами
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = stringResource(id = page.labelResId))
                        }
                    )
                }
            }

            //ViewPager (HorizontalPager) для перелистывания между вкладками
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                // Определение типа привычек на основе текущей позиции в пейджере
                val habitType = pages[pageIndex]
                HabitListByTypeScreen(
                    type = habitType,
                    viewModel = viewModel,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
fun HabitListByTypeScreen(
    type: HabitType,
    viewModel: HabitListViewModel,
    onNavigate: (String) -> Unit
) {
    val habits by viewModel.habits.collectAsState()

    val habitsOfType = habits.filter { it.type == type }
    if (habitsOfType.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.add_habit),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(habitsOfType) { habit ->
            HabitItem(habit = habit) {
                onNavigate(habit.id)
            }
        }
    }
}

@Composable
fun HabitItem(habit: Habit, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(habit.color.value)
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.headlineSmall,
                color = if (habit.color.luminance() < 0.5) Color.White else Color.Black
            )
            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodyLarge,
                color = if (habit.color.luminance() < 0.5) Color.White else Color.Black,
            )
        }
    }
}
