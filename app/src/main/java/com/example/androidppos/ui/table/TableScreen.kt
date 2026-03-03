package com.hd.hdmobilepos.androidppos.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hd.hdmobilepos.androidppos.domain.TableStatus
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

private val HyGreen = Color(0xFF014437)
private val HyBeige = Color(0xFFBFA27A)
private val BgGray = Color(0xFFF3F3F3)

@Composable
fun TableRoute(viewModel: TableViewModel) {
    val state by viewModel.uiState.collectAsState()
    val selectedTable = state.tables.firstOrNull { it.id == state.selectedTableId }
    val selectedElapsed = selectedTable?.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
    val selectedTotal = state.selectedOrderLines.sumOf { it.priceSnapshot * it.qty }

    Column(modifier = Modifier.fillMaxSize().background(BgGray)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                state.areas.forEach { area ->
                    val selected = state.selectedAreaId == area.id
                    Column(modifier = Modifier.clickable { viewModel.onAction(TableUiAction.SelectArea(area.id)) }) {
                        Text(
                            area.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (selected) HyGreen else Color(0xFF4E6660),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (selected) Divider(color = HyGreen, thickness = 2.dp)
                    }
                }
            }
            Text("⚙ 테이블 편집", color = Color(0xFF6B6B6B), style = MaterialTheme.typography.titleMedium)
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.72f).fillMaxHeight().padding(20.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.tables) { table ->
                        val selected = table.id == state.selectedTableId
                        val elapsed = table.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
                        val amountText = NumberFormat.getNumberInstance(Locale.KOREA).format(table.totalAmount ?: 0)
                        val cardColor = when (table.status) {
                            TableStatus.OCCUPIED -> HyGreen
                            TableStatus.BILLING -> HyBeige
                            TableStatus.DISABLED -> Color(0xFFD7D7D7)
                            TableStatus.EMPTY -> Color.White
                        }
                        val textColor = if (table.status == TableStatus.EMPTY) Color(0xFF333333) else Color.White
                        Card(
                            modifier = Modifier.fillMaxWidth().height(126.dp).clickable { viewModel.onAction(TableUiAction.SelectTable(table.id)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 8.dp else 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(table.name, color = textColor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("${amountText}원", color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${elapsed}분 · ${table.capacity}명", color = textColor.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(0.28f).fillMaxHeight().background(Color.White)) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(HyGreen).padding(vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(selectedTable?.name ?: "T-1", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("식사중 ${selectedElapsed}분 | ${selectedTable?.capacity ?: 0}명", color = Color.White.copy(alpha = 0.95f))
                }

                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.selectedOrderLines.forEach { line ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(line.nameSnapshot, style = MaterialTheme.typography.titleMedium)
                            Text("${line.qty}", style = MaterialTheme.typography.titleMedium)
                        }
                        Divider()
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("총 주문금액", style = MaterialTheme.typography.titleLarge, color = Color(0xFF666666))
                    Text(
                        "${NumberFormat.getNumberInstance(Locale.KOREA).format(selectedTotal)}원",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFD73737),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HyBeige)
                ) { Text("결제", style = MaterialTheme.typography.headlineSmall) }
            }
        }
    }
}
