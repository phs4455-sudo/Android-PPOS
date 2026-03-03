package com.hd.hdmobilepos.andriodppos.ui.table

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hd.hdmobilepos.andriodppos.domain.TableStatus
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

private val HyGreen = Color(0xFF014437)
private val HyBeige = Color(0xFFBFA27A)
private val CanvasGray = Color(0xFFF2F2F2)

@Composable
fun TableRoute(viewModel: TableViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            state.areas.forEach { area ->
                val selected = state.selectedAreaId == area.id
                Column(modifier = Modifier.clickable { viewModel.onAction(TableUiAction.SelectArea(area.id)) }) {
                    Text(
                        text = area.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) HyGreen else Color(0xFF36514C)
                    )
                    if (selected) Box(Modifier.fillMaxWidth().height(3.dp).background(HyGreen))
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize().background(CanvasGray)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(220.dp),
                modifier = Modifier.weight(2.2f).fillMaxHeight().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(state.tables) { table ->
                    val selected = state.selectedTableId == table.id
                    val elapsed = table.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
                    val amountText = NumberFormat.getNumberInstance(Locale.KOREA).format(table.totalAmount ?: 0)
                    val isCircle = table.id % 5L == 4L
                    val cardColor = when (table.status) {
                        TableStatus.OCCUPIED -> HyGreen
                        TableStatus.BILLING -> HyBeige
                        TableStatus.DISABLED -> Color(0xFFCECECE)
                        TableStatus.EMPTY -> Color.White
                    }
                    val textColor = if (table.status == TableStatus.EMPTY) Color(0xFF3C3C3C) else Color.White

                    Card(
                        shape = if (isCircle) CircleShape else RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isCircle) 300.dp else 170.dp)
                            .clickable { viewModel.onAction(TableUiAction.SelectTable(table.id)) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(table.name, style = MaterialTheme.typography.headlineSmall, color = textColor, fontWeight = FontWeight.Bold)
                            Text("${amountText}원", style = MaterialTheme.typography.headlineSmall, color = textColor, fontWeight = FontWeight.Bold)
                            Text("경과 ${elapsed}분  ·  ${table.capacity}명", color = textColor.copy(alpha = 0.9f))
                        }
                    }
                }
            }

            val selectedTable = state.tables.firstOrNull { it.id == state.selectedTableId }
            val elapsed = selectedTable?.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
            val total = state.selectedOrderLines.sumOf { it.priceSnapshot * it.qty }
            val totalText = NumberFormat.getNumberInstance(Locale.KOREA).format(total)

            Column(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White)) {
                Surface(color = HyGreen, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(selectedTable?.name ?: "T-1", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("식사중 ${elapsed}분 | ${selectedTable?.capacity ?: 0}명", color = Color.White.copy(alpha = 0.9f))
                    }
                }

                Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.selectedOrderLines.forEach { line ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(line.nameSnapshot, style = MaterialTheme.typography.titleMedium)
                            Text("${line.qty}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val from = state.selectedTableId ?: return@Button
                            val to = state.tables.firstOrNull { it.id != from }?.id ?: return@Button
                            viewModel.onAction(TableUiAction.MoveOrder(from, to))
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("이동") }
                    Button(
                        onClick = {
                            val source = state.selectedTableId ?: return@Button
                            val target = state.tables.firstOrNull { it.id != source && it.status == TableStatus.OCCUPIED }?.id ?: return@Button
                            viewModel.onAction(TableUiAction.MergeTables(source, target))
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("합석") }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("총 주문금액", style = MaterialTheme.typography.titleLarge, color = Color(0xFF666666))
                    Text("${totalText}원", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFD73737), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HyBeige)
                ) {
                    Text("결제", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}
