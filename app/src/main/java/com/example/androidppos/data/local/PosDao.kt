package com.hd.hdmobilepos.andriodppos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hd.hdmobilepos.andriodppos.domain.CartItem
import com.hd.hdmobilepos.andriodppos.domain.OrderStatus
import com.hd.hdmobilepos.andriodppos.domain.TableStatus
import kotlinx.coroutines.flow.Flow

data class TableWithOrderSummary(
    val id: Long,
    val name: String,
    val status: TableStatus,
    val totalAmount: Long?,
    val createdAt: Long?,
    val capacity: Int
)

data class OrderLineSummary(
    val id: Long,
    val nameSnapshot: String,
    val priceSnapshot: Long,
    val qty: Int
)

@Dao
interface PosDao {
    @Query("SELECT COUNT(*) FROM areas")
    suspend fun getAreaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAreas(areas: List<AreaEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<TableEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<MenuCategoryEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Query("SELECT * FROM areas ORDER BY sortOrder")
    fun observeAreas(): Flow<List<AreaEntity>>

    @Query(
        """
        SELECT t.id, t.name, t.status, o.totalAmount, o.createdAt, t.capacity
        FROM tables t
        LEFT JOIN orders o ON o.tableId = t.id AND o.status IN ('CREATED', 'SENT')
        WHERE t.areaId = :areaId
        ORDER BY t.sortOrder
        """
    )
    fun observeTableSummaries(areaId: Long): Flow<List<TableWithOrderSummary>>

    @Query(
        """
        SELECT oi.id, oi.nameSnapshot, oi.priceSnapshot, oi.qty
        FROM order_items oi
        INNER JOIN orders o ON o.id = oi.orderId
        WHERE o.tableId = :tableId AND o.status IN ('CREATED', 'SENT')
        ORDER BY oi.id
        """
    )
    fun observeOrderLinesForTable(tableId: Long): Flow<List<OrderLineSummary>>

    @Query("SELECT * FROM menu_categories WHERE courtId = :courtId ORDER BY sortOrder")
    fun observeCategories(courtId: Long): Flow<List<MenuCategoryEntity>>

    @Query("SELECT * FROM menu_items WHERE categoryId = :categoryId ORDER BY sortOrder")
    fun observeMenuItems(categoryId: Long): Flow<List<MenuItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Update
    suspend fun updateTable(table: TableEntity)

    @Query("SELECT * FROM tables WHERE id = :tableId")
    suspend fun getTable(tableId: Long): TableEntity?

    @Query("SELECT * FROM orders WHERE tableId = :tableId AND status IN ('CREATED', 'SENT') LIMIT 1")
    suspend fun getActiveOrderForTable(tableId: Long): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity>

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: Long)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Long)

    @Transaction
    suspend fun createOrderForTable(tableId: Long, cart: List<CartItem>) {
        if (cart.isEmpty()) return
        val total = cart.sumOf { it.price * it.qty }
        val now = System.currentTimeMillis()
        val existingOrder = getActiveOrderForTable(tableId)
        if (existingOrder == null) {
            val orderId = insertOrder(
                OrderEntity(
                    tableId = tableId,
                    createdAt = now,
                    status = OrderStatus.CREATED,
                    totalAmount = total
                )
            )
            insertOrderItems(
                cart.map {
                    OrderItemEntity(
                        orderId = orderId,
                        menuItemId = it.menuItemId,
                        nameSnapshot = it.name,
                        priceSnapshot = it.price,
                        qty = it.qty
                    )
                }
            )
        } else {
            val merged = (getOrderItems(existingOrder.id) + cart.map {
                OrderItemEntity(
                    orderId = existingOrder.id,
                    menuItemId = it.menuItemId,
                    nameSnapshot = it.name,
                    priceSnapshot = it.price,
                    qty = it.qty
                )
            }).groupBy { it.menuItemId to it.priceSnapshot }.map { (key, items) ->
                OrderItemEntity(
                    orderId = existingOrder.id,
                    menuItemId = key.first,
                    nameSnapshot = items.first().nameSnapshot,
                    priceSnapshot = key.second,
                    qty = items.sumOf { it.qty }
                )
            }
            deleteOrderItems(existingOrder.id)
            insertOrderItems(merged)
            updateOrder(existingOrder.copy(totalAmount = merged.sumOf { it.priceSnapshot * it.qty }))
        }
        getTable(tableId)?.let { updateTable(it.copy(status = TableStatus.OCCUPIED)) }
    }

    @Transaction
    suspend fun moveOrder(fromTableId: Long, toTableId: Long) {
        val order = getActiveOrderForTable(fromTableId) ?: return
        val toTable = getTable(toTableId) ?: return
        if (toTable.status == TableStatus.DISABLED) return

        updateOrder(order.copy(tableId = toTableId))
        getTable(fromTableId)?.let { updateTable(it.copy(status = TableStatus.EMPTY)) }
        updateTable(toTable.copy(status = TableStatus.OCCUPIED))
    }

    @Transaction
    suspend fun mergeTables(sourceTableId: Long, targetTableId: Long) {
        val sourceOrder = getActiveOrderForTable(sourceTableId) ?: return
        val targetOrder = getActiveOrderForTable(targetTableId) ?: return
        val targetTable = getTable(targetTableId) ?: return
        if (targetTable.status == TableStatus.DISABLED) return

        val sourceItems = getOrderItems(sourceOrder.id)
        val targetItems = getOrderItems(targetOrder.id)
        val mergedByMenu = (sourceItems + targetItems)
            .groupBy { it.menuItemId to it.priceSnapshot }
            .map { (key, items) ->
                OrderItemEntity(
                    orderId = targetOrder.id,
                    menuItemId = key.first,
                    nameSnapshot = items.first().nameSnapshot,
                    priceSnapshot = key.second,
                    qty = items.sumOf { it.qty }
                )
            }

        deleteOrderItems(targetOrder.id)
        insertOrderItems(mergedByMenu)

        val newTotal = mergedByMenu.sumOf { it.priceSnapshot * it.qty }
        updateOrder(targetOrder.copy(totalAmount = newTotal))

        deleteOrderItems(sourceOrder.id)
        deleteOrder(sourceOrder.id)

        getTable(sourceTableId)?.let { updateTable(it.copy(status = TableStatus.EMPTY)) }
        updateTable(targetTable.copy(status = TableStatus.OCCUPIED))
    }
}
