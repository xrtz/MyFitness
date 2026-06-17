package com.example.myfitness.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfitness.domain.models.DayFoodItemModel

@Composable
fun DayInfoView(dayFoodItem: DayFoodItemModel, caloriesGoal: Float) {
    val proteinGoal = caloriesGoal * 0.30f / 4f
    val fatsGoal = caloriesGoal * 0.25f / 9f
    val carbsGoal = caloriesGoal * 0.45f / 4f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CaloriesCircle(consumed = dayFoodItem.calories, goal = caloriesGoal)

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MacroBar(
                    label = "Белки",
                    current = dayFoodItem.protein,
                    goal = proteinGoal,
                    color = Color(0xFF60A5FA)
                )
                MacroBar(
                    label = "Жиры",
                    current = dayFoodItem.fats,
                    goal = fatsGoal,
                    color = Color(0xFFFB923C)
                )
                MacroBar(
                    label = "Углев.",
                    current = dayFoodItem.carbohydrates,
                    goal = carbsGoal,
                    color = Color(0xFF4ADE80)
                )
            }
        }
    }
}

@Composable
private fun CaloriesCircle(consumed: Float, goal: Float) {
    val safeGoal = goal.coerceAtLeast(1f)
    val progress = (consumed / safeGoal).coerceIn(0f, 1f)
    val isOver = consumed > goal
    val arcColor =
        if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryContainer

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "caloriesProgress"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = stroke
            )
            if (animatedProgress > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    style = stroke
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = consumed.toInt().toString(),
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "ккал",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "из ${goal.toInt()}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun MacroBar(label: String, current: Float, goal: Float, color: Color) {
    val progress = (current / goal.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "macroProgress_$label"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(52.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${current.toInt()}/${goal.toInt()}г",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.width(80.dp)
        )
    }
}
