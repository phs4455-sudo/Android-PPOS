package com.hd.hdmobilepos.andriodppos.ui.foodcourt

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import java.text.NumberFormat
import java.util.Locale

private val HyGreen = Color(0xFF014437)
private val HyBeige = Color(0xFFBFA27A)
private val PageGray = Color(0xFFF2F2F2)

@Composable
fun FoodCourtRoute(viewModel: FoodCourtViewModel) {
    val state by viewModel.uiState.collectAsState()
    val total = state.cartItems.sumOf { it.price * it.qty }
    val totalText = NumberFormat.getNumberInstance(Locale.KOREA).format(total)

    Column(modifier = Modifier.fillMaxSize().background(PageGray)) {
        Row(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White).padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Table ${state.selectedTableId}", style = MaterialTheme.typography.headlineMedium, color = HyGreen, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("상품명", color = Color(0xFF777777))
                    Text("수량", color = Color(0xFF777777))
                    Text("금액", color = Color(0xFF777777))
                }
                state.cartItems.forEach { cart ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(cart.name, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.titleMedium)
                        Text("${cart.qty}", modifier = Modifier.weight(0.3f))
                        Text(
                            NumberFormat.getNumberInstance(Locale.KOREA).format(cart.price * cart.qty),
                            modifier = Modifier.weight(0.7f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("✕", modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.ChangeQty(cart.menuItemId, -cart.qty)) })
                    }
                }

                Box(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("받을 금액", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${totalText}원", style = MaterialTheme.typography.headlineLarge, color = Color(0xFFD73737), fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.weight(2.1f).fillMaxHeight().padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxWidth()) {
                    state.categories.forEach { category ->
                        val selected = state.selectedCategoryId == category.id
                        Column(modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.SelectCategory(category.id)) }) {
                            Text(
                                category.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (selected) HyGreen else Color(0xFF35554E),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (selected) Box(Modifier.fillMaxWidth().height(2.dp).background(HyGreen))
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(180.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 12.dp).weight(1f)
                ) {
                    items(state.menuItems) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().height(105.dp).clickable { viewModel.onAction(FoodCourtAction.AddToCart(item)) }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(NumberFormat.getNumberInstance(Locale.KOREA).format(item.price), color = HyGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionButton("반품/환불", Color(0xFF6C8A9E), Modifier.weight(1f))
            ActionButton("주문 보류", HyGreen, Modifier.weight(1f))
            ActionButton("결제 진행", HyBeige, Modifier.weight(1.4f)) { viewModel.onAction(FoodCourtAction.CreateOrder) }
        }
    }
}

@Composable
private fun ActionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp).clip(RoundedCornerShape(10.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, style = MaterialTheme.typography.headlineSmall)
    }
}
