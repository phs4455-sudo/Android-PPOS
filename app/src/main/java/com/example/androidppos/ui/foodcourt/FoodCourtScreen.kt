package com.example.androidppos.ui.foodcourt

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FoodCourtRoute(viewModel: FoodCourtViewModel) {
    val state by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.categories.forEach { category ->
                Text(text = category.name, modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.SelectCategory(category.id)) })
            }
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(120.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(state.menuItems) { item ->
                Card(modifier = Modifier.fillMaxWidth().clickable { viewModel.onAction(FoodCourtAction.AddToCart(item)) }) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(item.name, style = MaterialTheme.typography.titleSmall)
                        Text("${item.price}원")
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("장바구니", style = MaterialTheme.typography.titleMedium)
            state.cartItems.forEach { cart ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${cart.name} x${cart.qty}")
                    Text("${cart.price * cart.qty}원")
                }
            }
            Button(onClick = { viewModel.onAction(FoodCourtAction.CreateOrder(tableId = 1L)) }) {
                Text("주문 생성")
            }
        }
    }
}
