package com.xeismonium.washcleaner.ui.report.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.xeismonium.washcleaner.core.utils.CurrencyFormatter

@Composable
fun ReportBarChart(
    data: Map<Float, Double>,
    labels: Map<Float, String>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(data.keys.toList(), data.values.toList())
            }
        }
    }

    val startAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        CurrencyFormatter.format(value.toDouble())
    }

    val bottomAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
        labels[value] ?: value.toString()
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = startAxisValueFormatter
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
