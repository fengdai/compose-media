package com.github.fengdai.compose.media.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun <T> Option(
    name: String,
    values: List<T>,
    currentValue: T,
    formatter: (T) -> String = { it.toString() },
    onValueChanged: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium
        )
        Row {
            Text(
                text = formatter(currentValue),
                style = MaterialTheme.typography.labelSmall
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                values.forEach { value ->
                    DropdownMenuItem(
                        text = {
                            Text(text = formatter(value))
                        },
                        onClick = {
                            onValueChanged(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BooleanOption(
    name: String,
    value: Boolean,
    enabled: Boolean = true,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = value,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onValueChange
            )
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        Switch(value, onValueChange, enabled = enabled)
    }
}
