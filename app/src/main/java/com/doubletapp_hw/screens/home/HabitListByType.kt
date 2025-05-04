package com.doubletapp_hw.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitApplication
import com.doubletapp_hw.R
import com.doubletapp_hw.enums.HabitType
import com.doubletapp_hw.screens.Routes
import com.doubletapp_hw.viewModels.HabitListViewModel
import com.doubletapp_hw.viewModels.ViewModelFactory
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListByType(type: HabitType) {
    val app = LocalContext.current.applicationContext as HabitApplication
    val navController = app.localNavController.current
    val viewModelFactory = ViewModelFactory(app)
    val habitListViewModel: HabitListViewModel = viewModel(factory = viewModelFactory)
    val habits by habitListViewModel.filteredHabits.collectAsStateWithLifecycle()

    val state = rememberPullToRefreshState()
    val isRefreshing = habitListViewModel.syncState.value ?: false

    val habitsOfType by remember(habits, type) {
        derivedStateOf { habits.filter { it.type == type } }
    }

    PullToRefreshBox(
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = { habitListViewModel.syncHabitsWithServer() },
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (habitsOfType.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(Modifier.padding(24.dp))
                        Text(
                            text = stringResource(R.string.add_habit),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

            } else {
                items(
                    items = habitsOfType,
                    key = { it.id }
                ) { habit ->
                    SwipeToDismissCardContainer(
                        item = habit, onDelete = { habitListViewModel.deleteHabit(habit) }) {
                        HabitCard(habit = habit) {
                            navController.navigate(Routes.HabitEdit(habit.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeToDismissCardContainer(
    item: Habit, onDelete: (Habit) -> Unit, card: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete(item)
                true
            } else {
                false
            }
        })

    SwipeToDismissBox(state = dismissState, modifier = Modifier, backgroundContent = {
        if (dismissState.dismissDirection.name == SwipeToDismissBoxValue.StartToEnd.name) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
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
    }, enableDismissFromEndToStart = false, content = { card() })
}

@Composable
fun HabitCard(habit: Habit, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ), colors = CardDefaults.cardColors(
            containerColor = Color(habit.color)
        ), modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.headlineSmall,
                color = if (Color(habit.color).luminance() < 0.5) Color.White else Color.Black
            )
            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodyLarge,
                color = if (Color(habit.color).luminance() < 0.5) Color.White else Color.Black,
            )
            Text(
                text = Instant.ofEpochMilli(habit.date)
                    .atZone(ZoneId.systemDefault())
                    .format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale("ru"))
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = if (Color(habit.color).luminance() < 0.5) Color.White else Color.Black,
            )
        }
    }
}