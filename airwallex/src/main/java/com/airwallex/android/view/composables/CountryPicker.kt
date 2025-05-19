package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.ui.composables.StandardSearchBar

@Composable
internal fun <T> CountryPicker(
    hint: String,
    options: List<T>,
    searchFilter: ((option: T, search: String) -> Boolean)? = null,
    content: @Composable (option: T, modifier: Modifier) -> Unit
) {
    var search by remember { mutableStateOf<String?>(null) }
    if (searchFilter != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(Modifier.padding(horizontal = 24.dp)) {
            StandardSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(32.dp, 128.dp),
                hint = hint,
                text = search.orEmpty(),
                onTextChanged = { search = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    LazyColumn {
        val filteredOptions = options
            .let { option ->
                if (!search.isNullOrBlank() && searchFilter != null) {
                    option.filter {
                        searchFilter(it, search.orEmpty())
                    }
                } else {
                    option
                }
            }

        filteredOptions.forEachIndexed { _, option ->
            item {
                content(
                    option,
                    Modifier.fillMaxWidth(),
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}