package com.example.myfitness.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    val fatsGoal    = caloriesGoal * 0.25f / 9f
    val carbsGoal   = caloriesGoal * 0.45f / 4f

    Card(
        modifier  = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier             = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment  = Alignment.CenterHorizontally,
            verticalArrangement  = Arrangement.spacedBy(12.dp)
        ) {
            CaloriesCircle(
                consumed     = dayFoodItem.calories,
                goal         = caloriesGoal
            )

            Column(
                modifier            = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MacroBar(label = "Б", current = dayFoodItem.protein,       goal = proteinGoal, color = Color(0xFF2196F3))
                MacroBar(label = "Ж", current = dayFoodItem.fats,          goal = fatsGoal,    color = Color(0xFFFFC107))
                MacroBar(label = "У", current = dayFoodItem.carbohydrates, goal = carbsGoal,   color = Color(0xFFFF9800))
            }
        }
    }
}

@Composable
private fun CaloriesCircle(consumed: Float, goal: Float) {
    val safeGoal  = goal.coerceAtLeast(1f)
    val progress  = (consumed / safeGoal).coerceIn(0f, 1f)
    val isOver    = consumed > goal
    val arcColor  = if (isOver) Color(0xFFFF5722) else Color(0xFF4CAF50)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color      = Color(0xFFE0E0E0),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter  = false,
                style      = stroke
            )
            if (progress > 0f) {
                drawArc(
                    color      = arcColor,
                    startAngle = 135f,
                    sweepAngle = 270f * progress,
                    useCenter  = false,
                    style      = stroke
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = consumed.toInt().toString(),
                fontSize   = 30.sp,
                fontWeight = FontWeight.Bold,
                color      = if (isOver) Color(0xFFFF5722) else Color(0xFF212121)
            )
            Text(text = "ккал", fontSize = 12.sp, color = Color.Gray)
            Text(text = "из ${goal.toInt()}", fontSize = 11.sp, color = Color(0xFF9E9E9E))
        }
    }
}

@Composable
private fun MacroBar(label: String, current: Float, goal: Float, color: Color) {
    val progress = (current / goal.coerceAtLeast(1f)).coerceIn(0f, 1f)

    Row(
        modifier        = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            modifier   = Modifier.width(18.dp),
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = color
        )
        Spacer(Modifier.width(6.dp))
        LinearProgressIndicator(
            progress     = { progress },
            modifier     = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color        = color,
            trackColor   = Color(0xFFE0E0E0)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text     = "${current.toInt()}/${goal.toInt()}г",
            fontSize = 11.sp,
            color    = Color.Gray,
            modifier = Modifier.width(76.dp)
        )
    }
}
