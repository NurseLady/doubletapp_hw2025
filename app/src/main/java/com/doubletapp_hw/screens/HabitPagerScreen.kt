package com.doubletapp_hw.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitApplication
import com.doubletapp_hw.HabitType
import com.doubletapp_hw.R
import com.doubletapp_hw.SortingType
import com.doubletapp_hw.viewModels.HabitListViewModel
import com.doubletapp_hw.viewModels.ViewModelFactory
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsPagerScreen(
    onNavigate: (String) -> Unit,
    showBottomSheet: Boolean,
    onShowBottomSheetChange: (Boolean) -> Unit
) {
    val application = LocalContext.current.applicationContext as HabitApplication
    val viewModelFactory = ViewModelFactory(application.habitRepository)
    val habitListViewModel: HabitListViewModel = viewModel(factory = viewModelFactory)

    val coroutineScope = rememberCoroutineScope()
    val pages = HabitType.entries.toList()
    val pagerState = rememberPagerState { pages.size }
    val sheetState = rememberModalBottomSheetState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigate("")
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp)
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
                    onNavigate = onNavigate
                )
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        onShowBottomSheetChange(false)
                    },
                    sheetState = sheetState
                ) {
                    FilterAndSearchFragment(
                        inputText = inputText,
                        onTextChange = { newText -> inputText = newText },
                        onSortingChange = { query: String, option: SortingType, isA: Boolean ->
                            habitListViewModel.applyFilters(query, option, isA)
                            onShowBottomSheetChange(false)
                        })
                }
            }
        }
    }
}

@Composable
fun HabitListByTypeScreen(
    type: HabitType,
    onNavigate: (String) -> Unit
) {
    val application = LocalContext.current.applicationContext as HabitApplication
    val viewModelFactory = ViewModelFactory(application.habitRepository)
    val habitListViewModel: HabitListViewModel = viewModel(factory = viewModelFactory)
    val habits by habitListViewModel.filteredHabits.collectAsState()

    val habitsOfType = habits.filter { it.type == type }
    Log.d("HabitList", "Filtered habits: ${habitsOfType.size}")
    if (habitsOfType.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.add_habit),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(
                items = habitsOfType,
                key = { it.id }
            ) { habit ->
                SwipeToDismissCardContainer(
                    item = habit,
                    onDelete = { habitListViewModel.deleteHabit(habit) }
                ) {
                    HabitCard(habit = habit) {
                        onNavigate(habit.id)
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeToDismissCardContainer(
    item: Habit,
    onDelete: (Habit) -> Unit,
    card: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete(item)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        backgroundContent = {
            if (dismissState.dismissDirection.name == SwipeToDismissBoxValue.StartToEnd.name) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults
                        .cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        },
        enableDismissFromEndToStart = false,
        content = { card() }
    )
}

@Composable
fun HabitCard(habit: Habit, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(habit.color)
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
                color = if (Color(habit.color).luminance() < 0.5) Color.White else Color.Black
            )
            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodyLarge,
                color = if (Color(habit.color).luminance() < 0.5) Color.White else Color.Black,
            )
            Text(
                text = habit.lastEdited.format(
                    DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale("ru"))
                ),
                style = MaterialTheme.typography.bodySmall,
                color = if (Color(habit.color).luminance() < 0.5) Color.White else Color.Black,
            )
        }
    }
}