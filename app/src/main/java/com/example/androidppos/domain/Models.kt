package com.hd.hdmobilepos.androidppos.domain

enum class TableStatus { EMPTY, OCCUPIED, BILLING, DISABLED }
enum class OrderStatus { CREATED, SENT, PAID, VOID }

data class CartItem(
    val menuItemId: Long,
    val name: String,
    val price: Long,
    val qty: Int = 1
)
