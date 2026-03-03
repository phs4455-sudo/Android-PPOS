package com.example.androidppos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidppos.data.local.PosDatabase
import com.example.androidppos.data.repository.PosRepository
import com.example.androidppos.ui.components.PosTopBar
import com.example.androidppos.ui.foodcourt.FoodCourtViewModel
import com.example.androidppos.ui.navigation.PosNavGraph
import com.example.androidppos.ui.table.TableViewModel
import com.example.androidppos.ui.theme.AndroidPPOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = PosRepository(PosDatabase.getInstance(this).posDao())

        setContent {
            AndroidPPOSTheme {
                val tableVm: TableViewModel = viewModel(factory = TableViewModel.Factory(repository))
                val foodVm: FoodCourtViewModel = viewModel(factory = FoodCourtViewModel.Factory(repository))
                Scaffold(topBar = { PosTopBar() }) { padding ->
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        PosNavGraph(tableViewModel = tableVm, foodCourtViewModel = foodVm)
                    }
                }
            }
        }
    }
}
