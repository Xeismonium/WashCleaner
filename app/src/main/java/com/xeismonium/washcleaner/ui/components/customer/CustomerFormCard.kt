package com.xeismonium.washcleaner.ui.components.customer

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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

@Composable
fun CustomerFormCard(
    name: String,
    phone: String,
    address: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNameInvalid = name.isBlank()
    val isPhoneInvalid = phone.isBlank()

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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Informasi Pelanggan",
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
                label = "Nama Pelanggan",
                placeholder = "Contoh: John Doe",
                leadingIcon = Icons.Default.Person,
                isRequired = true,
                isError = isNameInvalid,
                errorMessage = if (isNameInvalid) "Nama pelanggan tidak boleh kosong" else null
            )

            // Phone Input
            ModernTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Nomor Telepon",
                placeholder = "Contoh: 08123456789",
                leadingIcon = Icons.Default.Phone,
                isRequired = true,
                isError = isPhoneInvalid,
                errorMessage = if (isPhoneInvalid) "Nomor telepon tidak boleh kosong" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            // Address Input
            ModernTextField(
                value = address,
                onValueChange = onAddressChange,
                label = "Alamat",
                placeholder = "Contoh: Jl. Example No. 123",
                leadingIcon = Icons.Default.LocationOn,
                singleLine = false,
                minLines = 3,
                maxLines = 5
            )
        }
    }
}
