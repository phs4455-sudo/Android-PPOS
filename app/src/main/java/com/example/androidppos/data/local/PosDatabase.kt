package com.hd.hdmobilepos.androidppos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hd.hdmobilepos.androidppos.domain.OrderStatus
import com.hd.hdmobilepos.androidppos.domain.TableStatus

@Database(
    entities = [
        AreaEntity::class,
        TableEntity::class,
        MenuCategoryEntity::class,
        MenuItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        OrderItemOptionEntity::class,
        PaymentEntity::class,
        PrintJobEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(PosTypeConverters::class)
abstract class PosDatabase : RoomDatabase() {
    abstract fun posDao(): PosDao

    companion object {
        @Volatile
        private var instance: PosDatabase? = null

        fun getInstance(context: Context): PosDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PosDatabase::class.java,
                    "ppos.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}

class PosTypeConverters {
    @TypeConverter
    fun toTableStatus(value: String): TableStatus = TableStatus.valueOf(value)

    @TypeConverter
    fun fromTableStatus(status: TableStatus): String = status.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = OrderStatus.valueOf(value)

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String = status.name
}
