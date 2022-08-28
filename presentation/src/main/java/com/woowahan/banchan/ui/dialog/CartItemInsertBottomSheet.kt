package com.woowahan.banchan.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.DialogCartAddBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.ui.viewmodel.CartItemInsertBottomSheetViewModel
import com.woowahan.domain.model.BaseBanchan
import kotlinx.coroutines.launch

class CartItemInsertBottomSheet: BottomSheetDialogFragment() {
    companion object {
        private const val EXTRA_BANCHAN = "extra_banchan"
        private const val TAG = "cart_bottom_sheet"
        fun get(banchan: BaseBanchan, insertListener: (BaseBanchan, Int) -> Unit): CartItemInsertBottomSheet {
            return CartItemInsertBottomSheet().apply {
                setInsertListener(insertListener)
                arguments = bundleOf(EXTRA_BANCHAN to banchan)
            }
        }
    }
    private var _binding: DialogCartAddBinding? = null
    private val binding get() = _binding ?: error("Binding not Initialized")

    private val viewModel: CartItemInsertBottomSheetViewModel by viewModels()

    private var insertListener: ((BaseBanchan, Int) -> (Unit))? = null
    fun setInsertListener(insertListener: (BaseBanchan, Int) -> (Unit)){
        this.insertListener = insertListener
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    fun show(fragmentManager: FragmentManager){
        if(fragmentManager.findFragmentByTag(TAG) == null)
            this.show(fragmentManager, TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCartAddBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        insertListener?.let { viewModel.insertListener = it }
        arguments?.let {
            viewModel.banchan = it.getSerializable(EXTRA_BANCHAN) as BaseBanchan
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when(it) {
                        is CartItemInsertBottomSheetViewModel.UiEvent.Dismiss -> {
                            this@CartItemInsertBottomSheet.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}