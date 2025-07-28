package com.soda1127.itbookstorecleanarchitecture.screen.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

abstract class BaseFragment<VM: BaseViewModel, VB: ViewBinding>: Fragment() {

    abstract val vm: VM

    abstract val binding: VB

    private var fetchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
    }

    open fun initState() {
        initViews()
        fetchJob = vm.fetchData()
        observeData()
    }

    abstract fun initViews()

    abstract fun observeData()

    override fun onDestroyView() {
        super.onDestroyView()
        if (fetchJob?.isActive == true) {
            fetchJob?.cancel()
        }
    }

}
