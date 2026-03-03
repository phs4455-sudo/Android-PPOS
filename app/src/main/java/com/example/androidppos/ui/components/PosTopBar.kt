package com.hd.hdmobilepos.andriodppos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PosTopBar() {
    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("THE HYUNDAI", style = MaterialTheme.typography.titleMedium)
                Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            }
        },
        actions = {
            Button(onClick = {}) { Text("점검") }
            Button(onClick = {}) { Text("조회") }
            Button(onClick = {}) { Text("영수증 재출력") }
            Button(onClick = {}) { Text("더보기") }
        }
    )
}
