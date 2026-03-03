package com.example.androidppos.data.repository

import android.util.Log
import com.example.androidppos.data.local.MenuCategoryEntity
import com.example.androidppos.data.local.MenuItemEntity
import com.example.androidppos.data.local.PosDao
import com.example.androidppos.data.local.TableWithOrderSummary
import com.example.androidppos.domain.CartItem
import kotlinx.coroutines.flow.Flow

interface ReceiptDriver {
    suspend fun printReceipt(orderId: Long, payload: String)
}

class FakeReceiptDriver : ReceiptDriver {
    override suspend fun printReceipt(orderId: Long, payload: String) {
        Log.i("FakeReceiptDriver", "printReceipt orderId=$orderId payload=$payload")
    }
}

class PosRepository(
    private val dao: PosDao,
    private val receiptDriver: ReceiptDriver = FakeReceiptDriver()
) {
    fun observeAreas() = dao.observeAreas()
    fun observeTableSummaries(areaId: Long): Flow<List<TableWithOrderSummary>> = dao.observeTableSummaries(areaId)
    fun observeCategories(courtId: Long): Flow<List<MenuCategoryEntity>> = dao.observeCategories(courtId)
    fun observeMenuItems(categoryId: Long): Flow<List<MenuItemEntity>> = dao.observeMenuItems(categoryId)

    suspend fun createOrder(tableId: Long, cart: List<CartItem>) {
        dao.createOrderForTable(tableId, cart)
        Log.i("PosRepository", "order_created tableId=$tableId items=${cart.size}")
    }

    suspend fun moveOrder(fromTableId: Long, toTableId: Long) {
        dao.moveOrder(fromTableId, toTableId)
        Log.i("PosRepository", "order_moved from=$fromTableId to=$toTableId")
    }

    suspend fun mergeTables(sourceTableId: Long, targetTableId: Long) {
        dao.mergeTables(sourceTableId, targetTableId)
        Log.i("PosRepository", "tables_merged source=$sourceTableId target=$targetTableId")
    }

    suspend fun printSimpleReceipt(orderId: Long, payload: String) {
        receiptDriver.printReceipt(orderId, payload)
    }
}
