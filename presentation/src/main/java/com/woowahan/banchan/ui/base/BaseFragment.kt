package com.woowahan.banchan.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.woowahan.banchan.LifeCycleLogObserver
import timber.log.Timber

abstract class BaseFragment<T: ViewDataBinding>: Fragment() {
    protected val TAG: String = this::class.java.simpleName

    private var _binding: T? = null
    protected val binding get() = _binding ?: error("Binding not Initialized")
    protected abstract val layoutResId: Int

    private val logLifecycleObserver = LifeCycleLogObserver(TAG)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag(TAG).i("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(logLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, layoutResId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag(TAG).i("onViewCreated")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Timber.tag(TAG).i("onViewStateRestored")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Timber.tag(TAG).i("onHiddenChanged to [$hidden]")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.tag(TAG).i("onSaveInstanceState")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        Timber.tag(TAG).i("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(logLifecycleObserver)
    }

    override fun onDetach() {
        super.onDetach()
        Timber.tag(TAG).i("onDetach")
    }
}