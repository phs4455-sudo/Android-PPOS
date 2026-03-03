package com.hd.hdmobilepos.androidppos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PosTopBar() {
    val now = LocalDateTime.now()
    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "THE HYUNDAI",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF014437)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        Text(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd (E)")), style = MaterialTheme.typography.bodySmall)
                        Text(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    TopBadge("포스 : 5556")
                    TopBadge("거래 : 0014")
                }
            }
        },
        actions = {
            OutlinedButton(onClick = {}) { Text("점검") }
            OutlinedButton(onClick = {}) { Text("조회") }
            OutlinedButton(onClick = {}) { Text("영수증 재출력") }
            OutlinedButton(onClick = {}) { Text("더보기") }
        }
    )
}

@Composable
private fun TopBadge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(Color(0xFFEAEAEA), RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    )
}
