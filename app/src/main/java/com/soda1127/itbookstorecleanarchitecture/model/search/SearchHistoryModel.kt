package com.soda1127.itbookstorecleanarchitecture.model.search

import androidx.compose.runtime.Immutable
import com.soda1127.itbookstorecleanarchitecture.model.CellType
import com.soda1127.itbookstorecleanarchitecture.model.Model

@Immutable
data class SearchHistoryModel(
    override val id: String,
    override val type: CellType = CellType.SEARCH_HISTORY_CELL,
    val text: String
): Model(id, type)
