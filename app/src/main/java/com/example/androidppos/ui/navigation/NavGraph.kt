package com.example.androidppos.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidppos.ui.foodcourt.FoodCourtRoute
import com.example.androidppos.ui.foodcourt.FoodCourtViewModel
import com.example.androidppos.ui.table.TableRoute
import com.example.androidppos.ui.table.TableViewModel

@Composable
fun PosNavGraph(
    tableViewModel: TableViewModel,
    foodCourtViewModel: FoodCourtViewModel
) {
    var route by rememberSaveable { mutableStateOf("tables") }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { route = "tables" }) { Text("Restaurant") }
            Button(onClick = { route = "foodcourt" }) { Text("Food") }
        }
        when (route) {
            "foodcourt" -> FoodCourtRoute(foodCourtViewModel)
            else -> TableRoute(tableViewModel)
        }
    }
}
