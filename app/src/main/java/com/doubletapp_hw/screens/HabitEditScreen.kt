package com.doubletapp_hw.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doubletapp_hw.Habit
import com.doubletapp_hw.HabitApplication
import com.doubletapp_hw.HabitPriority
import com.doubletapp_hw.HabitType
import com.doubletapp_hw.R
import com.doubletapp_hw.viewModels.HabitEditViewModel
import com.doubletapp_hw.viewModels.ViewModelFactory
import java.time.LocalDateTime

@Composable
fun HabitEditScreen(habitId: String, onBack: () -> Unit) {
    val application = LocalContext.current.applicationContext as HabitApplication
    val viewModelFactory = ViewModelFactory(application.habitRepository)
    val habitEditViewModel: HabitEditViewModel = viewModel(factory = viewModelFactory)
    val habitState by habitEditViewModel.habit.observeAsState(initial = null)

    val isFirstLoad = remember { mutableStateOf(true) }

    if (isFirstLoad.value) {
        LaunchedEffect(habitId) {
            habitEditViewModel.loadHabitById(habitId)
            isFirstLoad.value = false
        }
    }

    val loading = habitState == null

    if (loading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        var habit by remember { mutableStateOf(habitState ?: Habit()) }
        val priorityOptions = HabitPriority.entries
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = habit.name,
                onValueChange = { habit = habit.copy(name = it) },
                label = { Text(stringResource(R.string.habit_name)) }
            )

            TextField(
                value = habit.description,
                onValueChange = { habit = habit.copy(description = it) },
                label = { Text(stringResource(R.string.description)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(stringResource(R.string.priority))
            DropdownMenuBox(
                priorityOptions.map { stringResource(it.labelResId) },
                habit.priority.ordinal
            ) { selectedIndex ->
                habit = habit.copy(priority = priorityOptions[selectedIndex])
            }

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                RadioButtonGroup(
                    // Передаём сами элементы enum как опции
                    options = listOf(
                        stringResource(R.string.positive),
                        stringResource(R.string.negative)
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
                    value = habit.frequency,
                    onValueChange = { habit = habit.copy(frequency = it) },
                    label = { Text(stringResource(R.string.frequency)) },
                    modifier = Modifier.weight(1f)
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
                    onClick = onBack
                ) {
                    Text(stringResource(R.string.back))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val updatedHabit = habit.copy(
                            id = habit.id,
                            lastEdited = LocalDateTime.now()
                        )
                        habitEditViewModel.saveHabit(updatedHabit)
                        onBack()
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}


@Composable
fun ColorCard(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    var isColorPickerExpanded by remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isColorPickerExpanded = !isColorPickerExpanded },
        colors = CardDefaults.cardColors(
            containerColor = selectedColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.pick_colour),
                    color = if (selectedColor.luminance() < 0.5) Color.White else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon( // Имитирует выпадающий список
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = if (isColorPickerExpanded) stringResource(R.string.drop_up)
                    else stringResource(R.string.drop_down),
                    tint = if (selectedColor.luminance() < 0.5) Color.White else Color.Black,
                    modifier = Modifier
                        .graphicsLayer(rotationX = if (isColorPickerExpanded) 180f else 0f)
                )
            }

            if (isColorPickerExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                // Элемент для выбора цвета
                ColorPicker(selectedColor = selectedColor, onColorSelected)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "RGB: (${(selectedColor.red * 255).toInt()}, " +
                            "${(selectedColor.green * 255).toInt()}, " +
                            "${(selectedColor.blue * 255).toInt()})\n" +
                            "HSV: ${selectedColor.toHSVString()}",
                    color = if (selectedColor.luminance() < 0.5) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = (0..15).map { Color.hsv(it * 360f / 16f, 1f, 1f) } // Цвета квадратов
    val backgroundBrush = remember { Brush.horizontalGradient(colors.map { it }) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundBrush)
            .padding(vertical = 16.dp, horizontal = 8.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            colors.forEach { color ->
                Box( // Комбо-вомбо из двух вложенных боксов чтоб обводка квадратов красивая была
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            color = if (color == selectedColor) Color.White
                            else Color(255, 255, 255, 127),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .background(
                                color = color,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        }
    }
}


private fun Color.toHSVString(): String {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        hsv
    )
    val h = hsv[0].toInt()
    val s = (hsv[1] * 255).toInt()
    val v = (hsv[2] * 255).toInt()
    return "($h, $s, $v)"
}