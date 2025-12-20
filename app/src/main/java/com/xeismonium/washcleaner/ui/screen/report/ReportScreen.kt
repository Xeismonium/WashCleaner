package com.xeismonium.washcleaner.ui.screen.report

import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.ui.components.common.WashCleanerScaffold
import com.xeismonium.washcleaner.ui.components.report.ReportStatsCard
import com.xeismonium.washcleaner.ui.components.report.RevenueChartCard
import com.xeismonium.washcleaner.ui.components.report.SegmentedButtons
import com.xeismonium.washcleaner.ui.components.report.SummaryCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    ReportContent(
        uiState = uiState,
        onPeriodChange = { period -> viewModel.changePeriod(period) },
        onRefresh = { viewModel.refresh() },
        onDownloadClick = {
            try {
                val csvContent = viewModel.generateCsvContent()
                val timestamp = System.currentTimeMillis()
                val filename = "Laporan_WashCleaner_$timestamp.csv"
                
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(csvContent.toByteArray())
                    }
                    Toast.makeText(context, "Laporan disimpan di Downloads", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Gagal menyimpan laporan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        },
        onMenuClick = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportContent(
    uiState: ReportUiState,
    onPeriodChange: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    WashCleanerScaffold(
        title = "Laporan Pendapatan",
        onMenuClick = onMenuClick,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SegmentedButtons(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodChange = onPeriodChange
                )
            }

            item {
                SummaryCard(
                    totalRevenue = uiState.totalRevenue,
                    trendPercentage = uiState.trendPercentage,
                    selectedPeriod = uiState.selectedPeriod
                )
            }

            item {
                RevenueChartCard(chartData = uiState.chartData)
            }

            item {
                ReportStatsCard(
                    title = "Transaksi Selesai",
                    value = "${uiState.totalTransactions}",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    modifier = Modifier.fillMaxSize()
                )
            }

            item {
                ReportStatsCard(
                    title = "Rata-rata / Transaksi",
                    value = CurrencyUtils.formatRupiah(uiState.averageTransactionValue),
                    icon = Icons.Default.Payments,
                    modifier = Modifier.fillMaxSize()
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unduh Laporan",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportPreview() {
    WashCleanerTheme {
        ReportContent(
            uiState = ReportUiState(
                totalRevenue = 1500000.0,
                totalTransactions = 15,
                averageTransactionValue = 100000.0,
                trendPercentage = 5.0,
                chartData = listOf(
                    ChartDataPoint("08:00", 750000.0, false),
                    ChartDataPoint("12:00", 300000.0, false),
                    ChartDataPoint("16:00", 900000.0, true),
                    ChartDataPoint("20:00", 550000.0, false)
                ),
                selectedPeriod = "day"
            )
        )
    }
}