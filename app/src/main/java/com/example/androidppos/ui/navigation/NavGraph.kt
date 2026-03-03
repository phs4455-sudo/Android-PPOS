package com.example.androidppos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidppos.ui.foodcourt.FoodCourtRoute
import com.example.androidppos.ui.foodcourt.FoodCourtViewModel
import com.example.androidppos.ui.table.TableRoute
import com.example.androidppos.ui.table.TableViewModel

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
