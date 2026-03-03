package com.hd.hdmobilepos.androidppos.ui.navigation

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
    foodCourtViewModel: FoodCourtViewModel,
    startDestination: String = "tables"
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("tables") { TableRoute(tableViewModel) }
        composable("foodcourt") { FoodCourtRoute(foodCourtViewModel) }
    }
}
