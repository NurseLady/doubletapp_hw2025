package com.doubletapp_hw.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitType
import com.doubletapp_hw.R
import com.doubletapp_hw.viewModels.HabitListViewModel
import kotlinx.coroutines.launch

@Composable
fun HabitsPagerScreen(onNavigate: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val pages = HabitType.entries.toList()
    val pagerState = rememberPagerState { pages.size }
    val habitListViewModel: HabitListViewModel = viewModel()

    val filterSheetState = rememberBottomSheetScaffoldState()
    val inputText = remember { mutableStateOf("") }

    BottomSheetScaffold(
        scaffoldState = filterSheetState,
        sheetContent = {
            FilterAndSearchSection(
                inputText = inputText.value,
                onTextChange = { newText -> inputText.value = newText },
                onSortingByNameChange = { f -> habitListViewModel.sortByName(f) },
                onSortingByDateChange = { f -> habitListViewModel.sortByDate(f) },
                onApplyFilters = {
                    habitListViewModel.applyFilters(inputText.value)
                }
            )
        },
        sheetPeekHeight = 64.dp,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onNavigate("new")
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = stringResource(page.labelResId)) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                HabitListByTypeScreen(
                    type = pages[pageIndex],
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
fun HabitListByTypeScreen(
    type: HabitType,
    onNavigate: (String) -> Unit
) {
    val habitListViewModel: HabitListViewModel = viewModel()
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
            items(habitsOfType) { habit ->
                HabitItem(habit = habit) {
                    onNavigate(habit.id)
                }
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

@Composable
fun FilterAndSearchSection(
    inputText: String,
    onTextChange: (String) -> Unit,
    onSortingByNameChange: (Boolean) -> Unit,
    onSortingByDateChange: (Boolean) -> Unit,
    onApplyFilters: () -> Unit
) {
    var isSortingNameAscending by remember { mutableStateOf(false) }
    var isSortingDateAscending by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        // Сортировка по алфавиту (по имени)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.sort_alphabetically), modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    isSortingNameAscending = !isSortingNameAscending
                    onSortingByNameChange(isSortingNameAscending)
                }
            ) {
                Icon(
                    imageVector = if (isSortingNameAscending) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "placeholder"
                )
            }
        }

        // Сортировка по дате (по времени редактирования)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.sort_edititme), modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    isSortingDateAscending = !isSortingDateAscending
                    onSortingByDateChange(isSortingDateAscending)
                }
            ) {
                Icon(
                    imageVector = if (isSortingDateAscending) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = ""
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = inputText,
            onValueChange = onTextChange,
            label = { Text(stringResource(R.string.search_by_name)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onApplyFilters() }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onApplyFilters,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.apply))
        }
    }
}