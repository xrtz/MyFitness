package com.example.myfitness.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfitness.presentation.viewmodel.HomeViewModel
import java.time.LocalDate

private const val PAGER_CENTER = 500

@Composable
fun WeekView(viewModel: HomeViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val today        = remember { LocalDate.now() }
    val todayWeekStart = remember { viewModel.getStartOfWeek(today) }

    val pagerState = rememberPagerState(
        initialPage = PAGER_CENTER,
        pageCount   = { PAGER_CENTER * 2 }
    )

    HorizontalPager(
        state    = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val weekOffset    = (page - PAGER_CENTER).toLong()
        val currentWeekStart = todayWeekStart.plusWeeks(weekOffset)

        Row(
            modifier              = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..6) {
                val date = currentWeekStart.plusDays(i.toLong())
                DayItem(
                    date       = date,
                    isSelected = date == selectedDate,
                    onClick    = { viewModel.selectDate(date) }
                )
            }
        }
    }
}

@Composable
fun DayItem(
    date       : LocalDate,
    isSelected : Boolean,
    onClick    : () -> Unit
) {
    val today   = remember { LocalDate.now() }
    val isToday = date == today

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text       = date.dayOfWeek.name.take(2),
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = when {
                isToday -> MaterialTheme.colorScheme.primary
                else    -> MaterialTheme.colorScheme.outline
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(38.dp)
                .background(
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday    -> MaterialTheme.colorScheme.primaryContainer
                        else       -> Color.Transparent
                    },
                    shape = CircleShape
                )
        ) {
            Text(
                text       = date.dayOfMonth.toString(),
                fontSize   = 14.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color      = when {
                    isSelected -> Color.White
                    isToday    -> MaterialTheme.colorScheme.primary
                    else       -> MaterialTheme.colorScheme.onBackground
                }
            )
        }
    }
}
