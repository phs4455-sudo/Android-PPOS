package com.hd.hdmobilepos.androidppos.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hd.hdmobilepos.androidppos.domain.OrderStatus
import com.hd.hdmobilepos.androidppos.domain.TableStatus

@Entity(tableName = "areas")
data class AreaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sortOrder: Int
)

@Entity(
    tableName = "tables",
    foreignKeys = [ForeignKey(
        entity = AreaEntity::class,
        parentColumns = ["id"],
        childColumns = ["areaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("areaId")]
)
data class TableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val areaId: Long,
    val name: String,
    val status: TableStatus,
    val capacity: Int,
    val sortOrder: Int
)

@Entity(tableName = "menu_categories")
data class MenuCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courtId: Long,
    val name: String,
    val sortOrder: Int
)

@Entity(
    tableName = "menu_items",
    foreignKeys = [ForeignKey(
        entity = MenuCategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("categoryId")]
)
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val price: Long,
    val isSoldOut: Boolean,
    val imageUrl: String?,
    val sortOrder: Int
)

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(
        entity = TableEntity::class,
        parentColumns = ["id"],
        childColumns = ["tableId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("tableId")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableId: Long?,
    val createdAt: Long,
    val status: OrderStatus,
    val totalAmount: Long
)

@Entity(
    tableName = "order_items",
    foreignKeys = [ForeignKey(
        entity = OrderEntity::class,
        parentColumns = ["id"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("orderId")]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val menuItemId: Long,
    val nameSnapshot: String,
    val priceSnapshot: Long,
    val qty: Int
)

@Entity(
    tableName = "order_item_options",
    foreignKeys = [ForeignKey(
        entity = OrderItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["orderItemId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("orderItemId")]
)
data class OrderItemOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderItemId: Long,
    val name: String,
    val priceDelta: Long
)

@Entity(
    tableName = "payments",
    foreignKeys = [ForeignKey(
        entity = OrderEntity::class,
        parentColumns = ["id"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("orderId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val method: String,
    val amount: Long,
    val status: String,
    val createdAt: Long
)

@Entity(
    tableName = "print_jobs",
    foreignKeys = [ForeignKey(
        entity = OrderEntity::class,
        parentColumns = ["id"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("orderId")]
)
data class PrintJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long?,
    val payload: String,
    val status: String,
    val createdAt: Long
)
