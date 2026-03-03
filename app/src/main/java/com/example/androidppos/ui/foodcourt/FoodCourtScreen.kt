package com.hd.hdmobilepos.andriodppos.ui.foodcourt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

@Composable
fun FoodCourtRoute(viewModel: FoodCourtViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("주문 리스트", style = MaterialTheme.typography.titleLarge)
                state.cartItems.forEach { cart ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${cart.name} x${cart.qty}")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("-", modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.ChangeQty(cart.menuItemId, -1)) })
                            Text("+", modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.ChangeQty(cart.menuItemId, +1)) })
                            Text("${cart.price * cart.qty}원")
                        }
                    }
                }
                Text("받는금액: ${state.cartItems.sumOf { it.price * it.qty }}원", style = MaterialTheme.typography.titleMedium)
            }

            Column(
                modifier = Modifier.weight(1.8f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.categories.forEach { category ->
                        val selected = state.selectedCategoryId == category.id
                        Card(
                            border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.SelectCategory(category.id)) }
                        ) {
                            Text(category.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                        }
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(130.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(state.menuItems) { item ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { viewModel.onAction(FoodCourtAction.AddToCart(item)) }) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(item.name, style = MaterialTheme.typography.titleSmall)
                                Text("${item.price}원")
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("테이블")
                    listOf(1L, 2L, 3L, 4L).forEach { tableId ->
                        Text(
                            "T$tableId",
                            modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.SelectTable(tableId)) }
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) { Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("반품/환불") } }
            Box(modifier = Modifier.weight(1f)) { Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("주문 보류") } }
            Box(modifier = Modifier.weight(1f)) { Button(onClick = { viewModel.onAction(FoodCourtAction.CreateOrder) }, modifier = Modifier.fillMaxWidth()) { Text("결제 진행") } }
        }
    }
}
