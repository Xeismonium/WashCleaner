package com.xeismonium.washcleaner.ui.components.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UnitSelector(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val units = listOf("kg", "item")
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(2.dp)
        ) {
            units.forEach { unit ->
                val isSelected = selectedUnit == unit
                TextButton(
                    onClick = { onUnitSelected(unit) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (isSelected) colorScheme.surface else Color.Transparent,
                        contentColor = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = if (unit == "kg") "Per KG" else "Per Item",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}