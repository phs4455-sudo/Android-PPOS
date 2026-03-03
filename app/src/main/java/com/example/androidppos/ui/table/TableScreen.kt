package com.hd.hdmobilepos.andriodppos.ui.table

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hd.hdmobilepos.andriodppos.domain.TableStatus
import java.util.concurrent.TimeUnit

@Composable
fun TableRoute(viewModel: TableViewModel) {
    val state by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.areas.forEach { area ->
                val selected = state.selectedAreaId == area.id
                Card(
                    border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier.clickable { viewModel.onAction(TableUiAction.SelectArea(area.id)) }
                ) {
                    Text(area.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1.8f).fillMaxHeight()
            ) {
                items(state.tables) { table ->
                    val selected = state.selectedTableId == table.id
                    Card(
                        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.onAction(TableUiAction.SelectTable(table.id)) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(table.name, style = MaterialTheme.typography.titleMedium)
                            Text("금액 ${table.totalAmount ?: 0}원")
                            val elapsed = table.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
                            Text("경과 ${elapsed}분")
                            Text("인원 ${table.capacity}명")
                            Text("상태 ${table.status}")
                        }
                    }
                }
            }

            val selectedTable = state.tables.firstOrNull { it.id == state.selectedTableId }
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(selectedTable?.name ?: "테이블 선택", style = MaterialTheme.typography.titleLarge)
                Text("상태: ${selectedTable?.status ?: TableStatus.EMPTY}")
                val elapsed = selectedTable?.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
                Text("경과: ${elapsed}분")
                Text("인원: ${selectedTable?.capacity ?: 0}명")
                Text("주문내역", style = MaterialTheme.typography.titleMedium)
                state.selectedOrderLines.forEach { line ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${line.nameSnapshot} x${line.qty}")
                        Text("${line.priceSnapshot * line.qty}원")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        val from = state.selectedTableId ?: return@Button
                        val to = state.tables.firstOrNull { it.id != from }?.id ?: return@Button
                        viewModel.onAction(TableUiAction.MoveOrder(from, to))
                    }) { Text("이동") }
                    Button(onClick = {
                        val source = state.selectedTableId ?: return@Button
                        val target = state.tables.firstOrNull { it.id != source && it.status == TableStatus.OCCUPIED }?.id ?: return@Button
                        viewModel.onAction(TableUiAction.MergeTables(source, target))
                    }) { Text("합석") }
                }
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("결제") }
            }
        }
    }
}
