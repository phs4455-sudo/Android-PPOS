package com.hd.hdmobilepos.androidppos.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hd.hdmobilepos.androidppos.ui.foodcourt.FoodCourtRoute
import com.hd.hdmobilepos.androidppos.ui.foodcourt.FoodCourtViewModel
import com.hd.hdmobilepos.androidppos.ui.table.TableRoute
import com.hd.hdmobilepos.androidppos.ui.table.TableViewModel

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
