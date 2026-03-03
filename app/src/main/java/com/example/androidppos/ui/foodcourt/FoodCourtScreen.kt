package com.hd.hdmobilepos.androidppos.ui.foodcourt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import java.text.NumberFormat
import java.util.Locale

private val HyGreen = Color(0xFF014437)
private val HyBeige = Color(0xFFBFA27A)
private val BgGray = Color(0xFFF3F3F3)

@Composable
fun FoodCourtRoute(viewModel: FoodCourtViewModel) {
    val state by viewModel.uiState.collectAsState()
    val total = state.cartItems.sumOf { it.price * it.qty }

    Column(modifier = Modifier.fillMaxSize().background(BgGray)) {
        BoxWithConstraints(modifier = Modifier.weight(1f).padding(10.dp)) {
            val leftPaneWidth = (maxWidth * 0.32f).coerceIn(300.dp, 420.dp)
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(
                    modifier = Modifier.width(leftPaneWidth).fillMaxHeight().background(Color.White).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Table ${state.selectedTableId}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = HyGreen)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("상품명", color = Color.Gray)
                        Text("수량", color = Color.Gray)
                        Text("금액", color = Color.Gray)
                    }
                    Divider()
                    state.cartItems.forEach { cart ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cart.name, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.titleMedium)
                            Text("${cart.qty}", modifier = Modifier.weight(0.3f))
                            Text(NumberFormat.getNumberInstance(Locale.KOREA).format(cart.price * cart.qty), modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold)
                            Text("✕", modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.ChangeQty(cart.menuItemId, -cart.qty)) })
                        }
                    }
                    Box(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("받을 금액", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(total)}원", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFD73737), fontWeight = FontWeight.Bold)
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        state.categories.forEach { category ->
                            val selected = state.selectedCategoryId == category.id
                            Column(modifier = Modifier.clickable { viewModel.onAction(FoodCourtAction.SelectCategory(category.id)) }) {
                                Text(
                                    category.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (selected) HyGreen else Color(0xFF4E6660),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (selected) Divider(color = HyGreen, thickness = 2.dp)
                            }
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(170.dp),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.menuItems) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth().height(106.dp).clickable { viewModel.onAction(FoodCourtAction.AddToCart(item)) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(NumberFormat.getNumberInstance(Locale.KOREA).format(item.price), color = HyGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FooterButton("반품/환불", Color(0xFF6C8A9E), Modifier.weight(1f))
            FooterButton("주문 보류", HyGreen, Modifier.weight(1f))
            FooterButton("결제 진행", HyBeige, Modifier.weight(1.4f)) { viewModel.onAction(FoodCourtAction.CreateOrder) }
        }
    }
}

@Composable
private fun FooterButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text, style = MaterialTheme.typography.headlineSmall)
    }
}
