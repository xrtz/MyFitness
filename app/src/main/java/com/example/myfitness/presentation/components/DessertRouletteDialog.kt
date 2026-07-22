package com.example.myfitness.presentation.components

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myfitness.domain.models.DessertModel
import com.example.myfitness.presentation.model.CommonDessertPresets
import com.example.myfitness.presentation.service.RouletteMusicService
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

private val ITEM_HEIGHT = 64.dp
private const val VISIBLE_ITEMS = 5
private const val CENTER_INDEX = VISIBLE_ITEMS / 2

private const val SPIN_CYCLES = 6
private const val START_CYCLE = 1
private const val SPIN_DURATION_MS = 3200

@Composable
fun DessertRouletteDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var reel by remember { mutableStateOf(buildReel()) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = reel.startFirstVisibleIndex
    )
    val scrollAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val itemHeightPx = with(density) { ITEM_HEIGHT.toPx() }

    var isSpinning by rememberSaveable { mutableStateOf(false) }
    var resultDessert by remember { mutableStateOf<DessertModel?>(null) }

    val stopMusic = {
        context.stopService(Intent(context, RouletteMusicService::class.java))
    }
    val dismissDialog = {
        stopMusic()
        onDismiss()
    }

    DisposableEffect(context) {
        onDispose { stopMusic() }
    }

    Dialog(onDismissRequest = dismissDialog) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Что сегодня поесть?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT * VISIBLE_ITEMS)
                ) {
                    LazyColumn(
                        state = listState,
                        userScrollEnabled = false,
                        contentPadding = PaddingValues(vertical = ITEM_HEIGHT * CENTER_INDEX)
                    ) {
                        items(reel.items.size) { index ->
                            DessertRow(dessert = reel.items[index])
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(ITEM_HEIGHT)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }

                Spacer(Modifier.height(16.dp))

                resultDessert?.let { dessert ->
                    Text(
                        text = "Сегодня: ${dessert.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))
                }

                FilledTonalButton(
                    enabled = !isSpinning,
                    onClick = {
                        isSpinning = true
                        resultDessert = null

                        context.startService(
                            Intent(context, RouletteMusicService::class.java)
                        )

                        val newReel = buildReel()
                        reel = newReel

                        coroutineScope.launch {
                            try {
                                scrollAnim.snapTo(0f)
                                listState.scrollToItem(newReel.startFirstVisibleIndex, 0)

                                val followJob = launch {
                                    snapshotFlow { scrollAnim.value }.collect { value ->
                                        val totalPx =
                                            newReel.startFirstVisibleIndex * itemHeightPx + value
                                        val index = (totalPx / itemHeightPx).toInt()
                                        val offset = totalPx - index * itemHeightPx
                                        listState.scrollToItem(index, offset.toInt())
                                    }
                                }

                                val distancePx =
                                    (newReel.targetFirstVisibleIndex -
                                        newReel.startFirstVisibleIndex) * itemHeightPx

                                scrollAnim.animateTo(
                                    targetValue = distancePx,
                                    animationSpec = tween(
                                        durationMillis = SPIN_DURATION_MS,
                                        easing = FastOutSlowInEasing
                                    )
                                )

                                followJob.cancelAndJoin()
                                listState.scrollToItem(newReel.targetFirstVisibleIndex, 0)
                                resultDessert = centeredDessert(listState, newReel.items)
                            } finally {
                                stopMusic()
                                isSpinning = false
                            }
                        }
                    }
                ) {
                    Text(if (isSpinning) "Крутится..." else "Крутить")
                }

                Spacer(Modifier.height(8.dp))

                FilledTonalButton(onClick = dismissDialog) {
                    Text("Закрыть")
                }
            }
        }
    }
}

private fun centeredDessert(
    listState: androidx.compose.foundation.lazy.LazyListState,
    items: List<DessertModel>
): DessertModel? {
    val layoutInfo = listState.layoutInfo
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    val closest = layoutInfo.visibleItemsInfo.minByOrNull { item ->
        kotlin.math.abs((item.offset + item.size / 2) - viewportCenter)
    }
    return closest?.let { items.getOrNull(it.index) }
}

private data class DessertReel(
    val items: List<DessertModel>,
    val startFirstVisibleIndex: Int,
    val targetFirstVisibleIndex: Int
)

private fun buildReel(): DessertReel {
    val shuffled = CommonDessertPresets.shuffled()
    val size = shuffled.size
    val items = List(START_CYCLE + SPIN_CYCLES + 1) { shuffled }.flatten()

    val startGlobalIndex = START_CYCLE * size
    val targetGlobalIndex = (START_CYCLE + SPIN_CYCLES) * size

    return DessertReel(
        items = items,
        startFirstVisibleIndex = startGlobalIndex - CENTER_INDEX,
        targetFirstVisibleIndex = targetGlobalIndex - CENTER_INDEX
    )
}

@Composable
private fun DessertRow(dessert: DessertModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ITEM_HEIGHT),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dessert.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
