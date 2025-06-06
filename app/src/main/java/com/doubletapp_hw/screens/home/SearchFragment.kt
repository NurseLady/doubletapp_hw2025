package com.doubletapp_hw.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.doubletapp_hw.R
import com.doubletapp_hw.enums.SortingType
import com.doubletapp_hw.screens.DropdownMenuBox

@Composable
fun FilterAndSearchFragment(
    onApply: (String, SortingType, Boolean) -> Unit
) {
    var selectedSortOption by remember { mutableStateOf(SortingType.NAME) }
    var isAscending by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    val sortOptions = SortingType.entries

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.sort_by), style = MaterialTheme.typography.titleMedium)
        DropdownMenuBox(
            options = sortOptions.map { stringResource(it.labelResId) },
            selectedIndex = sortOptions.indexOf(selectedSortOption),
            onSelect = { index -> selectedSortOption = sortOptions[index] }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.sort_direction),
            style = MaterialTheme.typography.titleMedium
        )
        DropdownMenuBox(
            options = listOf(
                stringResource(R.string.ascending),
                stringResource(R.string.descending)
            ),
            selectedIndex = if (isAscending) 0 else 1,
            onSelect = { index -> isAscending = index == 0 }
        )

        Spacer(modifier = Modifier.height(16.dp))


        TextField(
            value = inputText,
            onValueChange = { newText -> inputText = newText },
            label = { Text(stringResource(R.string.search_by_name)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onApply(inputText, selectedSortOption, isAscending) }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onApply(inputText, selectedSortOption, isAscending)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.apply))
        }
    }
}