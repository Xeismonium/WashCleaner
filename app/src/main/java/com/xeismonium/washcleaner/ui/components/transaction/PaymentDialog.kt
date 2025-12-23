package com.xeismonium.washcleaner.ui.components.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.util.CurrencyUtils
import com.xeismonium.washcleaner.util.PaymentUtils
import com.xeismonium.washcleaner.util.PaymentValidationResult

@Composable
fun PaymentDialog(
    transaction: LaundryTransactionEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var paymentAmount by remember { mutableStateOf("") }
    val remainingAmount = PaymentUtils.getRemainingAmount(
        transaction.paidAmount,
        transaction.totalPrice
    )

    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Tambah Pembayaran",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Current payment info
                PaymentProgressIndicator(
                    paidAmount = transaction.paidAmount,
                    totalPrice = transaction.totalPrice
                )

                HorizontalDivider()

                // Remaining amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sisa Tagihan:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = CurrencyUtils.formatRupiah(remainingAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Payment input
                OutlinedTextField(
                    value = paymentAmount,
                    onValueChange = {
                        paymentAmount = it
                        validationError = null
                    },
                    label = { Text("Jumlah Pembayaran") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = validationError != null,
                    supportingText = validationError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { paymentAmount = (remainingAmount / 2).toString() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("50%")
                    }
                    OutlinedButton(
                        onClick = { paymentAmount = remainingAmount.toString() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lunas")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = paymentAmount.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        validationError = "Masukkan jumlah yang valid"
                        return@Button
                    }

                    val validation = PaymentUtils.validatePaymentAmount(
                        transaction.paidAmount,
                        amount,
                        transaction.totalPrice
                    )

                    when (validation) {
                        is PaymentValidationResult.Valid -> onConfirm(amount)
                        is PaymentValidationResult.Invalid -> validationError = validation.message
                        is PaymentValidationResult.Overpayment -> {
                            validationError = "Kelebihan bayar: ${CurrencyUtils.formatRupiah(validation.amount)}"
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
