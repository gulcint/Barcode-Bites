package com.barcodebite.android.ui.screen.result

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.barcodebite.shared.domain.model.Nutrition
import kotlin.math.roundToInt

private data class NutritionMetric(
    val label: String,
    val value: Double,
    val maxDaily: Double,
    val unit: String,
    val color: Color,
)

private data class RiskSegment(
    val label: String,
    val value: Double,
    val maxDaily: Double,
    val color: Color,
)

@Composable
fun NutritionChart(
    nutrition: Nutrition,
    grade: String,
    modifier: Modifier = Modifier,
) {
    var animate by remember(nutrition) { mutableStateOf(false) }
    LaunchedEffect(nutrition) {
        animate = true
    }

    val chartProgress by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "chart-progress",
    )

    val metrics = remember(nutrition) {
        listOf(
            NutritionMetric("Kalori", nutrition.calories, 2000.0, "kcal", Color(0xFF457B9D)),
            NutritionMetric("Karbonhidrat", nutrition.carbohydrates, 275.0, "g", Color(0xFF2A9D8F)),
            NutritionMetric("Protein", nutrition.protein, 50.0, "g", Color(0xFF264653)),
            NutritionMetric("Yağ", nutrition.fat, 70.0, "g", Color(0xFFE9C46A)),
        )
    }

    val riskSegments = remember(nutrition) {
        listOf(
            RiskSegment("Şeker", nutrition.sugar, 50.0, Color(0xFFE76F51)),
            RiskSegment("Tuz", nutrition.salt, 5.0, Color(0xFF6D597A)),
            RiskSegment("Yağ", nutrition.fat, 70.0, Color(0xFFF4A261)),
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Besin Dağılımı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Skor notu: $grade",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            RiskDonut(
                riskSegments = riskSegments,
                progress = chartProgress,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            riskSegments.forEach { segment ->
                LegendRow(
                    color = segment.color,
                    label = segment.label,
                    value = "${segment.value} g / ${segment.maxDaily} g",
                )
            }

            metrics.forEachIndexed { index, metric ->
                NutritionMetricBar(
                    metric = metric,
                    animationProgress = chartProgress,
                    rowIndex = index,
                )
            }
        }
    }
}

@Composable
private fun RiskDonut(
    riskSegments: List<RiskSegment>,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val total = riskSegments
        .sumOf { (it.value / it.maxDaily).coerceAtLeast(0.0) }
        .coerceAtLeast(0.0001)

    Box(
        modifier = modifier.size(172.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(172.dp)) {
            val strokeWidth = 18.dp.toPx()
            val diameter = size.minDimension
            val arcSize = Size(diameter, diameter)
            val arcTopLeft = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f,
            )

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            var currentAngle = -90f
            riskSegments.forEach { segment ->
                val ratio = (segment.value / segment.maxDaily).coerceAtLeast(0.0)
                val sweep = ((ratio / total) * 360f).toFloat() * progress

                drawArc(
                    color = segment.color,
                    startAngle = currentAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                currentAngle += ((ratio / total) * 360f).toFloat()
            }
        }

        Text(
            text = "Risk",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun LegendRow(
    color: Color,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NutritionMetricBar(
    metric: NutritionMetric,
    animationProgress: Float,
    rowIndex: Int,
) {
    val targetProgress = (metric.value / metric.maxDaily).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress * animationProgress,
        animationSpec = tween(
            durationMillis = 900,
            delayMillis = rowIndex * 90,
            easing = FastOutSlowInEasing,
        ),
        label = "metric-progress-${metric.label}",
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = metric.label,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "${metric.value} ${metric.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            color = metric.color,
            trackColor = metric.color.copy(alpha = 0.2f),
        )

        Text(
            text = "%${(targetProgress * 100).roundToInt()} günlük referans",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
