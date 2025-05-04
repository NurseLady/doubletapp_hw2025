package com.doubletapp_hw.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitApplication
import com.doubletapp_hw.R
import com.doubletapp_hw.enums.HabitPriority
import com.doubletapp_hw.enums.HabitType
import com.doubletapp_hw.screens.DropdownMenuBox
import com.doubletapp_hw.screens.RadioButtonGroup
import com.doubletapp_hw.viewModels.HabitEditViewModel
import com.doubletapp_hw.viewModels.ViewModelFactory

@Composable
fun HabitEditScreen(habitId: String) {
    val app = LocalContext.current.applicationContext as HabitApplication
    val navController = app.localNavController.current
    val viewModelFactory =
        ViewModelFactory(app, habitId)
    val habitEditViewModel: HabitEditViewModel = viewModel(factory = viewModelFactory)
    val habitState by habitEditViewModel.habit.observeAsState(initial = null)

    val loading = habitState == null

    if (loading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else {
        var habit by remember { mutableStateOf(habitState ?: Habit()) }
        val priorityOptions = HabitPriority.entries
        val scrollState = rememberScrollState()
        var textFrequencyValue by remember { mutableStateOf(habit.frequency?.toString() ?: "") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = habit.title,
                onValueChange = { habit = habit.copy(title = it) },
                label = { Text(stringResource(R.string.habit_name)) })

            TextField(
                value = habit.description,
                onValueChange = { habit = habit.copy(description = it) },
                label = { Text(stringResource(R.string.description)) })

            Spacer(modifier = Modifier.height(8.dp))

            Text(stringResource(R.string.priority))
            DropdownMenuBox(
                priorityOptions.map { stringResource(it.labelResId) }, habit.priority.ordinal
            ) { selectedIndex ->
                habit = habit.copy(priority = priorityOptions[selectedIndex])
            }

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                RadioButtonGroup(
                    // Передаём сами элементы enum как опции
                    options = listOf(
                        stringResource(R.string.positive), stringResource(R.string.negative)
                    ),
                    // Выбираем индекс текущего типа
                    selectedOption = stringResource(habit.type.labelResId)
                ) { selectedIndex ->
                    // Устанавливаем тип привычки на основе выбранного индекса
                    habit = when (selectedIndex) {
                        "Положительная" -> habit.copy(type = HabitType.POSITIVE)
                        else -> habit.copy(type = HabitType.NEGATIVE)
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = habit.period,
                    onValueChange = { habit = habit.copy(period = it) },
                    label = { Text(stringResource(R.string.period)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = textFrequencyValue,
                    onValueChange = { input ->
                        // Оставляем только цифры в тексте
                        val filteredInput = input.filter { it.isDigit() }

                        textFrequencyValue = filteredInput.toIntOrNull()?.toString() ?: ""

                        habit.frequency = filteredInput.toIntOrNull()

                    },
                    label = { Text(stringResource(R.string.frequency)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Ограничиваем ввод клавиатурой для чисел
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ColorCard(selectedColor = Color(habit.color), onColorSelected = {
                habit = habit.copy(color = it.toArgb())
            })

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigateUp() }) {
                    Text(stringResource(R.string.back))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val updatedHabit = habit.copy(
                            id = habit.id, date = System.currentTimeMillis()
                        )
                        habitEditViewModel.saveHabit(updatedHabit)
                        navController.navigateUp()
                    }) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}