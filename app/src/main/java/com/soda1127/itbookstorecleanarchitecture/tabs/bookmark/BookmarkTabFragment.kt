package com.soda1127.itbookstorecleanarchitecture.tabs.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.soda1127.itbookstorecleanarchitecture.databinding.FragmentBookmarkTabBinding
import com.soda1127.itbookstorecleanarchitecture.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkTabFragment: Fragment() {

    private val binding: FragmentBookmarkTabBinding by viewBinding(FragmentBookmarkTabBinding::inflate)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    companion object {
        const val TAG = "BookmarkTabFragment"

        fun newInstance(bundle: Bundle = bundleOf()) = BookmarkTabFragment().apply {
            arguments = bundle
        }
    }

}
