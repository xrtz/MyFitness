package com.example.myfitness.presentation.components

import android.util.Log
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfitness.domain.models.DayFoodItemModel

@Composable
fun TypeOfMealView(name: String, dayFoodItem: DayFoodItemModel, type: Int) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .clickable(onClick = {}), elevation =   CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = name, color = Color.Black, fontSize = 22.sp)
                Card() {
                    Row(
                        modifier = Modifier.width(200.dp), // костыль
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(modifier = Modifier.wrapContentHeight()) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 4.dp
                                ), text = "1400 ccal", color = Color.Black, fontSize = 10.sp
                            )
                        }
                        Card(modifier = Modifier.wrapContentHeight()) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 4.dp
                                ), text = "1 б", color = Color.Black, fontSize = 10.sp
                            )
                        }
                        Card(modifier = Modifier.wrapContentHeight()) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 4.dp
                                ), text = "120 у", color = Color.Black, fontSize = 10.sp
                            )
                        }
                        Card(modifier = Modifier.wrapContentHeight()) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 4.dp
                                ), text = "300 ж", color = Color.Black, fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.size(64.dp),
                onClick = {Log.i("TAG", "clicked")}
            ) {
                Text(text = "+")
            }
        }
    }
}