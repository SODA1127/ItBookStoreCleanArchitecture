package com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.search

import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.AdapterListener

interface SearchHistoryListener: AdapterListener {

    fun onClickSearchHistoryItem(keyword: String)

    fun onClickRemoveItem(keyword: String)

}
