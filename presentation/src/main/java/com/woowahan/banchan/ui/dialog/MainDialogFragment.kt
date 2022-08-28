package com.woowahan.banchan.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.woowahan.banchan.databinding.DialogMainBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.ui.viewmodel.MainDialogViewModel
import kotlinx.coroutines.flow.collect

class MainDialogFragment: DialogFragment() {
    companion object {
        private const val TAG = "main_dialog_fragment"
        private const val EXTRA_MESSAGE = "extra_message"
        private const val EXTRA_POSITIVE_TITLE = "extra_positive_title"
        private const val EXTRA_NEGATIVE_TITLE = "extra_negative_title"

        fun get(
            message: String,
            positive: (Pair<String, ()->Unit>),
            negative: (Pair<String, ()->Unit>)? = null
        ): MainDialogFragment {
            return MainDialogFragment().apply {
                arguments = bundleOf(
                    EXTRA_MESSAGE to message,
                    EXTRA_POSITIVE_TITLE to positive.first,
                    EXTRA_NEGATIVE_TITLE to negative?.first
                )
                setPositiveBtn(positive.second)
                negative?.second?.let { setNegativeBtn(it) }
            }
        }
        
    }

    private var _binding: DialogMainBinding? = null
    private val binding get() = _binding ?: error("Binding not Initialized")

    private val viewModel: MainDialogViewModel by viewModels()

    private var positiveListener: (()->Unit)? = null
    private var negativeListener: (()->Unit)? = null

    fun show(fragmentManager: FragmentManager){
        if(fragmentManager.findFragmentByTag(TAG) == null)
            this.show(fragmentManager, TAG)
    }

    fun setPositiveBtn(positiveListener : (() -> Unit)){
        this.positiveListener = positiveListener
    }

    fun setNegativeBtn(negativeListener : (() -> Unit)){
        this.negativeListener = negativeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMainBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        arguments?.getString(EXTRA_MESSAGE)?.let { viewModel.setMessage(it) }
        arguments?.getString(EXTRA_POSITIVE_TITLE)?.let { viewModel.setPositiveBtn(it to positiveListener) }
        arguments?.getString(EXTRA_NEGATIVE_TITLE)?.let { viewModel.setNegativeBtn(it to negativeListener) }

        binding.viewModel = viewModel

        viewLifecycleOwner.repeatOnStarted {
            viewModel.eventFlow.collect {
                when(it) {
                    is MainDialogViewModel.UiEvent.Dismiss -> {
                        this@MainDialogFragment.dismiss()
                    }
                }
            }
        }

        return binding.root
    }


}