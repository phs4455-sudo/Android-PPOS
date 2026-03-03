package com.example.androidppos.ui.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidppos.data.local.AreaEntity
import com.example.androidppos.data.local.TableWithOrderSummary
import com.example.androidppos.data.repository.PosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TableUiState(
    val areas: List<AreaEntity> = emptyList(),
    val selectedAreaId: Long? = null,
    val tables: List<TableWithOrderSummary> = emptyList(),
    val loading: Boolean = true,
    val errorMessage: String? = null
)

sealed interface TableUiAction {
    data class SelectArea(val areaId: Long) : TableUiAction
    data class MoveOrder(val fromTableId: Long, val toTableId: Long) : TableUiAction
    data class MergeTables(val sourceTableId: Long, val targetTableId: Long) : TableUiAction
}

class TableViewModel(private val repository: PosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    init {
        observeAreasAndTables()
    }

    private fun observeAreasAndTables() {
        viewModelScope.launch {
            repository.observeAreas().collectLatest { areas ->
                val selectedAreaId = _uiState.value.selectedAreaId ?: areas.firstOrNull()?.id
                _uiState.value = _uiState.value.copy(areas = areas, selectedAreaId = selectedAreaId, loading = false)
                selectedAreaId?.let { areaId ->
                    repository.observeTableSummaries(areaId).collectLatest { tables ->
                        _uiState.value = _uiState.value.copy(tables = tables, loading = false)
                    }
                }
            }
        }
    }

    fun onAction(action: TableUiAction) {
        when (action) {
            is TableUiAction.SelectArea -> {
                _uiState.value = _uiState.value.copy(selectedAreaId = action.areaId)
                viewModelScope.launch {
                    repository.observeTableSummaries(action.areaId).collectLatest { tables ->
                        _uiState.value = _uiState.value.copy(tables = tables)
                    }
                }
            }
            is TableUiAction.MoveOrder -> viewModelScope.launch {
                runCatching { repository.moveOrder(action.fromTableId, action.toTableId) }
                    .onFailure { _uiState.value = _uiState.value.copy(errorMessage = "이동 실패: ${it.message}") }
            }
            is TableUiAction.MergeTables -> viewModelScope.launch {
                runCatching { repository.mergeTables(action.sourceTableId, action.targetTableId) }
                    .onFailure { _uiState.value = _uiState.value.copy(errorMessage = "합석 실패: ${it.message}") }
            }
        }
    }

    class Factory(private val repository: PosRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TableViewModel(repository) as T
    }
}
