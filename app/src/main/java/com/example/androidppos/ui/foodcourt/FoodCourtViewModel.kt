package com.hd.hdmobilepos.andriodppos.ui.foodcourt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hd.hdmobilepos.andriodppos.data.local.MenuCategoryEntity
import com.hd.hdmobilepos.andriodppos.data.local.MenuItemEntity
import com.hd.hdmobilepos.andriodppos.data.repository.PosRepository
import com.hd.hdmobilepos.andriodppos.domain.CartItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class FoodCourtUiState(
    val courtId: Long = 1L,
    val categories: List<MenuCategoryEntity> = emptyList(),
    val selectedCategoryId: Long? = null,
    val menuItems: List<MenuItemEntity> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val selectedTableId: Long = 1L,
    val errorMessage: String? = null
)

sealed interface FoodCourtAction {
    data class SelectCategory(val categoryId: Long) : FoodCourtAction
    data class AddToCart(val item: MenuItemEntity) : FoodCourtAction
    data class ChangeQty(val menuItemId: Long, val delta: Int) : FoodCourtAction
    data class SelectTable(val tableId: Long) : FoodCourtAction
    data object CreateOrder : FoodCourtAction
}

class FoodCourtViewModel(private val repository: PosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodCourtUiState())
    val uiState: StateFlow<FoodCourtUiState> = _uiState.asStateFlow()

    private var menuJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedIfNeeded()
            repository.observeCategories(_uiState.value.courtId).collectLatest { categories ->
                val selected = _uiState.value.selectedCategoryId ?: categories.firstOrNull()?.id
                _uiState.value = _uiState.value.copy(categories = categories, selectedCategoryId = selected)
                selected?.let(::loadMenu)
            }
        }
    }

    private fun loadMenu(categoryId: Long) {
        menuJob?.cancel()
        menuJob = viewModelScope.launch {
            repository.observeMenuItems(categoryId).collectLatest { items ->
                _uiState.value = _uiState.value.copy(menuItems = items)
            }
        }
    }

    fun onAction(action: FoodCourtAction) {
        when (action) {
            is FoodCourtAction.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = action.categoryId)
                loadMenu(action.categoryId)
            }
            is FoodCourtAction.AddToCart -> {
                val current = _uiState.value.cartItems.toMutableList()
                val idx = current.indexOfFirst { it.menuItemId == action.item.id }
                if (idx >= 0) current[idx] = current[idx].copy(qty = current[idx].qty + 1)
                else current.add(CartItem(action.item.id, action.item.name, action.item.price, 1))
                _uiState.value = _uiState.value.copy(cartItems = current)
            }
            is FoodCourtAction.ChangeQty -> {
                val updated = _uiState.value.cartItems.mapNotNull {
                    if (it.menuItemId != action.menuItemId) return@mapNotNull it
                    val newQty = it.qty + action.delta
                    if (newQty <= 0) null else it.copy(qty = newQty)
                }
                _uiState.value = _uiState.value.copy(cartItems = updated)
            }
            is FoodCourtAction.SelectTable -> _uiState.value = _uiState.value.copy(selectedTableId = action.tableId)
            is FoodCourtAction.CreateOrder -> viewModelScope.launch {
                runCatching { repository.createOrder(_uiState.value.selectedTableId, _uiState.value.cartItems) }
                    .onSuccess { _uiState.value = _uiState.value.copy(cartItems = emptyList(), errorMessage = null) }
                    .onFailure { _uiState.value = _uiState.value.copy(errorMessage = "주문 실패: ${it.message}") }
            }
        }
    }

    class Factory(private val repository: PosRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = FoodCourtViewModel(repository) as T
    }
}
