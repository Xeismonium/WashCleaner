package com.xeismonium.washcleaner.ui.components.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.components.form.ModernTextField
import com.xeismonium.washcleaner.ui.components.form.UnitSelector

@Composable
fun ServiceFormCard(
    name: String,
    price: String,
    unit: String,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNameInvalid = name.isBlank()
    val isPriceInvalid = price.isNotBlank() && (price.toIntOrNull() == null || (price.toIntOrNull() ?: 0) <= 0)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalLaundryService,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Informasi Layanan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(4.dp))

            // Name Input
            ModernTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Nama Layanan",
                placeholder = "Contoh: Cuci Kering Setrika",
                leadingIcon = Icons.Default.LocalLaundryService,
                isRequired = true,
                isError = isNameInvalid,
                errorMessage = if (isNameInvalid) "Nama layanan tidak boleh kosong" else null
            )

            // Price Input
            ModernTextField(
                value = price,
                onValueChange = { onPriceChange(it.filter { char -> char.isDigit() }) },
                label = "Harga",
                placeholder = "8000",
                leadingIcon = Icons.Default.AttachMoney,
                isRequired = true,
                isError = isPriceInvalid,
                errorMessage = if (isPriceInvalid) "Harga harus berupa angka lebih dari 0" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Unit Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Satuan Harga",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                UnitSelector(
                    selectedUnit = unit,
                    onUnitSelected = onUnitChange
                )
            }
        }
    }
}
