package com.example.myfitness.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfitness.presentation.viewmodel.CalendarViewModel
import java.time.LocalDate

@Composable
fun WeekView(viewModel: CalendarViewModel) {

    val selectedDate by viewModel.selectedDate.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = 500
        ,pageCount = { 1000 }
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->

        val currentWeekStart = viewModel
            .getStartOfWeek(LocalDate.now())
            .plusWeeks((page - 500).toLong())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            for (i in 0..6) {
                val date = currentWeekStart.plusDays(i.toLong())
                DayItem(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { viewModel.selectDate(date) }
                )
            }
        }
    }
}