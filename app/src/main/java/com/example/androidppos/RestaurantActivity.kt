package com.hd.hdmobilepos.androidppos

import android.content.Context
import android.content.Intent
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
import com.hd.hdmobilepos.androidppos.data.local.PosDatabase
import com.hd.hdmobilepos.androidppos.data.repository.PosRepository
import com.hd.hdmobilepos.androidppos.ui.components.PosTopBar
import com.hd.hdmobilepos.androidppos.ui.table.TableRoute
import com.hd.hdmobilepos.androidppos.ui.table.TableViewModel
import com.hd.hdmobilepos.androidppos.ui.theme.AndroidPPOSTheme

class RestaurantActivity : ComponentActivity() {
    companion object {
        fun newIntent(context: Context): Intent = Intent(context, RestaurantActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = PosRepository(PosDatabase.getInstance(this).posDao())

        setContent {
            AndroidPPOSTheme {
                val tableVm: TableViewModel = viewModel(factory = TableViewModel.Factory(repository))
                Scaffold(topBar = { PosTopBar() }) { padding ->
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        TableRoute(viewModel = tableVm)
                    }
                }
            }
        }
    }
}
