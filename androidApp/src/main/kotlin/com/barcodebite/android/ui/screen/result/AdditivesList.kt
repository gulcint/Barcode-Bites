package com.barcodebite.android.ui.screen.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.barcodebite.shared.domain.model.Additive

@Composable
fun AdditivesList(
    additives: List<Additive>,
    modifier: Modifier = Modifier,
) {
    if (additives.isEmpty()) {
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Katki Maddeleri",
            style = MaterialTheme.typography.titleMedium,
        )

        additives.forEach { additive ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = riskColor(additive.riskLevel).copy(alpha = 0.14f),
                        shape = RoundedCornerShape(14.dp),
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = additive.code.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = additive.riskLevel,
                        color = riskColor(additive.riskLevel),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Text(
                    text = additive.name,
                    style = MaterialTheme.typography.bodyLarge,
                )
                additive.description?.takeIf { it.isNotBlank() }?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun riskColor(riskLevel: String): Color {
    return when (riskLevel.lowercase()) {
        "high" -> Color(0xFFB00020)
        "moderate" -> Color(0xFFC77600)
        "low" -> Color(0xFF2D6A4F)
        else -> Color(0xFF5C677D)
    }
}
