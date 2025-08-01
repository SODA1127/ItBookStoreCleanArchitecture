package com.soda1127.itbookstorecleanarchitecture.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryEntity(
    @PrimaryKey val searchKeyword: String,
    val searchTimestamp: Long
)
