package com.doubletapp_hw

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import com.doubletapp_hw.ui.theme.Dobletapp_hwTheme

class SecondActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val habit = intent.extras?.getSerializable("habit", Habit::class.java) ?: Habit()

        setContent {
            Dobletapp_hwTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitEditScreen(habit)
                }
            }
        }
    }

    @Composable
    fun HabitEditScreen(habit: Habit) {
        var name by remember { mutableStateOf(habit.name) }
        var description by remember { mutableStateOf(habit.description) }
        val priorityOptions = listOf(getString(R.string.low), getString(R.string.mid), getString(R.string.hight))
        var priority by remember { mutableIntStateOf(habit.priority) }
        var type by remember { mutableStateOf(habit.type) }
        var period by remember { mutableStateOf(habit.period) }
        var frequency by remember { mutableStateOf(habit.frequency) }
        var selectedColor by remember { mutableStateOf(habit.color) }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(getString(R.string.habit_name)) }
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(getString(R.string.description)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(getString(R.string.priority))
            ExposedDropdownMenuBox(priorityOptions, priority) { selectedIndex ->
                priority = selectedIndex
            }


            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                RadioButtonGroup(listOf(getString(R.string.positive), getString(R.string.negative)), type) { selectedType ->
                    type = selectedType
                }
            }

            Row( // Поля 'выполнения' и 'периодичность' в одной группе
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = period,
                    onValueChange = { period = it },
                    label = { Text(getString(R.string.period)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text(getString(R.string.frequency)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Элемент для отображения цвета и выбора нового
            ColorCard(selectedColor, onColorSelected = { selectedColor = it })

            Row( // Группа кнопок для сохранения и отмены
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                ) {
                    Text(getString(R.string.back))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val i = Intent()
                        habit.name = name
                        habit.description = description
                        habit.period = period
                        habit.frequency = frequency
                        habit.priority = priority
                        habit.type = type
                        habit.color = selectedColor
                        i.putExtra("habit", habit)
                        setResult(RESULT_OK, i)
                        finish()
                    }
                ) {
                    Text(getString(R.string.save))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExposedDropdownMenuBox(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = options[selectedIndex],
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEachIndexed { index, option ->
                        DropdownMenuItem(onClick = {
                            onSelect(index)
                            expanded = false
                        }, text = { Text(option) })
                    }
                }
            }
        }
    }

    @Composable
    fun RadioButtonGroup(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
        Column {
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = { onOptionSelected(option) }
                    )
                    Text(text = option)
                }
            }
        }
    }

    @Composable
    fun ColorCard(selectedColor: Color, onColorSelected: (Color) -> Unit){
        var isColorPickerExpanded by remember { mutableStateOf(false) } // Отслеживание, открыт ли элемент
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isColorPickerExpanded = !isColorPickerExpanded },
            colors = CardDefaults.cardColors(
                containerColor = selectedColor // Выбранный цвет задаёт фон карточки
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
                        text = getString(R.string.pick_colour),
                        color = if (selectedColor.luminance() < 0.5) Color.White else Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Icon( // Имитирует выпадающий список
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = if (isColorPickerExpanded) getString(R.string.drop_up) else getString(R.string.drop_down),
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
                        text = "RGB: (${(selectedColor.red * 255).toInt()}, ${(selectedColor.green * 255).toInt()}, ${(selectedColor.blue * 255).toInt()})\n" +
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
        val backgroundBrush = Brush.horizontalGradient(colors.map { it }) // Градиент для фона

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
                                color = if (color == selectedColor) Color.White else Color(255, 255, 255, 127),
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
}