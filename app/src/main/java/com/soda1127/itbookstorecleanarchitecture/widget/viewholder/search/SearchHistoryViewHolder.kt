package com.soda1127.itbookstorecleanarchitecture.widget.viewholder.search

import com.soda1127.itbookstorecleanarchitecture.model.search.SearchHistoryModel
import com.soda1127.itbookstorecleanarchitecture.widget.adapter.listener.search.SearchHistoryListener
import com.soda1127.itbookstorecleanarchitecture.widget.viewholder.ModelViewHolder
import com.soda1127.itbookstorecleanarchitecture.databinding.ViewholderSearchHistoryBinding

class SearchHistoryViewHolder(
    private val binding: ViewholderSearchHistoryBinding
): ModelViewHolder<SearchHistoryModel, SearchHistoryListener>(binding) {

    override fun reset() = Unit

    override fun bindData(model: SearchHistoryModel) {
        super.bindData(model)
        binding.searchHistoryTextView.text = model.text
    }

    override fun bindViews(model: SearchHistoryModel, listener: SearchHistoryListener) {
        binding.root.setOnClickListener {
            listener.onClickSearchHistoryItem(model.text)
        }
        binding.removeImageView.setOnClickListener {
            listener.onClickRemoveItem(model.text)
        }
    }


}
