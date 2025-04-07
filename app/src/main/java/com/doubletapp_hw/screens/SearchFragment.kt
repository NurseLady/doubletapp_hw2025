package com.doubletapp_hw.screens

import androidx.compose.foundation.background
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
import com.doubletapp_hw.SortingType

@Composable
fun FilterAndSearchFragment(
    inputText: String,
    onTextChange: (String) -> Unit,
    onSortingChange: (String, SortingType, Boolean) -> Unit
) {
    var selectedSortOption by remember { mutableStateOf(SortingType.NAME) }
    var isAscending by remember { mutableStateOf(true) }
    val sortOptions = SortingType.entries

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
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
            onValueChange = onTextChange,
            label = { Text(stringResource(R.string.search_by_name)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSortingChange(inputText, selectedSortOption, isAscending) }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSortingChange(inputText, selectedSortOption, isAscending)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.apply))
        }
    }
}