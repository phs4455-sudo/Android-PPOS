package com.example.androidppos.ui.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidppos.domain.TableStatus
import java.util.concurrent.TimeUnit

@Composable
fun TableRoute(viewModel: TableViewModel) {
    val state by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.areas.forEach { area ->
                Text(
                    text = area.name,
                    modifier = Modifier.clickable { viewModel.onAction(TableUiAction.SelectArea(area.id)) }
                )
            }
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(140.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.tables) { table ->
                Card(modifier = Modifier.fillMaxWidth().clickable { }) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(table.name, style = MaterialTheme.typography.titleMedium)
                        Text("상태: ${table.status}")
                        Text("금액: ${table.totalAmount ?: 0}원")
                        val elapsed = table.createdAt?.let { TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - it) } ?: 0
                        Text("경과: ${elapsed}분")
                        if (table.status == TableStatus.BILLING) Text("결제대기", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
