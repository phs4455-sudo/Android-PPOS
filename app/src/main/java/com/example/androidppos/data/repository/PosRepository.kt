package com.hd.hdmobilepos.andriodppos.data.repository

import android.util.Log
import com.hd.hdmobilepos.andriodppos.data.local.AreaEntity
import com.hd.hdmobilepos.andriodppos.data.local.MenuCategoryEntity
import com.hd.hdmobilepos.andriodppos.data.local.MenuItemEntity
import com.hd.hdmobilepos.andriodppos.data.local.OrderLineSummary
import com.hd.hdmobilepos.andriodppos.data.local.PosDao
import com.hd.hdmobilepos.andriodppos.data.local.TableEntity
import com.hd.hdmobilepos.andriodppos.data.local.TableWithOrderSummary
import com.hd.hdmobilepos.andriodppos.domain.CartItem
import com.hd.hdmobilepos.andriodppos.domain.TableStatus
import com.example.androidppos.data.local.AreaEntity
import com.example.androidppos.data.local.MenuCategoryEntity
import com.example.androidppos.data.local.MenuItemEntity
import com.example.androidppos.data.local.OrderLineSummary
import com.example.androidppos.data.local.PosDao
import com.example.androidppos.data.local.TableEntity
import com.example.androidppos.data.local.TableWithOrderSummary
import com.example.androidppos.domain.CartItem
import com.example.androidppos.domain.TableStatus
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
    suspend fun seedIfNeeded() {
        if (dao.getAreaCount() > 0) return

        val areaIds = dao.insertAreas(
            listOf(
                AreaEntity(name = "식당가 1층 홀", sortOrder = 1),
                AreaEntity(name = "식당가 1층 룸", sortOrder = 2),
                AreaEntity(name = "식당가 2층 홀", sortOrder = 3),
                AreaEntity(name = "야외 테라스", sortOrder = 4)
            )
        )
        dao.insertTables(
            areaIds.flatMapIndexed { idx, areaId ->
                (1..6).map { number ->
                    TableEntity(
                        areaId = areaId,
                        name = "T${idx + 1}0$number",
                        status = if (number == 6) TableStatus.DISABLED else TableStatus.EMPTY,
                        capacity = if (number % 2 == 0) 4 else 2,
                        sortOrder = number
                    )
                }
            }
        )

        val categoryIds = dao.insertCategories(
            listOf(
                MenuCategoryEntity(courtId = 1L, name = "한식", sortOrder = 1),
                MenuCategoryEntity(courtId = 1L, name = "양식", sortOrder = 2),
                MenuCategoryEntity(courtId = 1L, name = "디저트", sortOrder = 3),
                MenuCategoryEntity(courtId = 1L, name = "음료", sortOrder = 4)
            )
        )

        dao.insertMenuItems(
            listOf(
                MenuItemEntity(categoryId = categoryIds[0], name = "비빔밥", price = 12000, isSoldOut = false, imageUrl = null, sortOrder = 1),
                MenuItemEntity(categoryId = categoryIds[0], name = "김치찌개", price = 10000, isSoldOut = false, imageUrl = null, sortOrder = 2),
                MenuItemEntity(categoryId = categoryIds[1], name = "함박스테이크", price = 14500, isSoldOut = false, imageUrl = null, sortOrder = 1),
                MenuItemEntity(categoryId = categoryIds[1], name = "크림파스타", price = 15500, isSoldOut = false, imageUrl = null, sortOrder = 2),
                MenuItemEntity(categoryId = categoryIds[2], name = "치즈케이크", price = 7500, isSoldOut = false, imageUrl = null, sortOrder = 1),
                MenuItemEntity(categoryId = categoryIds[2], name = "아이스크림", price = 5500, isSoldOut = false, imageUrl = null, sortOrder = 2),
                MenuItemEntity(categoryId = categoryIds[3], name = "아메리카노", price = 4500, isSoldOut = false, imageUrl = null, sortOrder = 1),
                MenuItemEntity(categoryId = categoryIds[3], name = "오렌지주스", price = 5000, isSoldOut = false, imageUrl = null, sortOrder = 2)
            )
        )
    }

    fun observeAreas() = dao.observeAreas()
    fun observeTableSummaries(areaId: Long): Flow<List<TableWithOrderSummary>> = dao.observeTableSummaries(areaId)
    fun observeOrderLinesForTable(tableId: Long): Flow<List<OrderLineSummary>> = dao.observeOrderLinesForTable(tableId)
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
