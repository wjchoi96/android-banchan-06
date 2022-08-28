package com.woowahan.banchan.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.DialogCartAddBinding
import com.woowahan.domain.model.BaseBanchan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class CartItemInsertBottomSheet constructor(
    private val banchan: BaseBanchan,
    private val insertListener: (BaseBanchan, Int) -> (Unit)
): BottomSheetDialogFragment() {
    companion object {
        const val TAG = "cart_bottom_sheet"
    }

    private var _binding: DialogCartAddBinding? = null
    private val binding get() = _binding ?: error("Binding not Initialized")

    private var _itemCount: MutableStateFlow<Int> = MutableStateFlow(1)
    val itemCount = _itemCount.asStateFlow()

    private var _cartCost: MutableStateFlow<Long> =
        MutableStateFlow(if(banchan.salePrice == 0L) banchan.price else banchan.salePrice)
    var cartCost = _cartCost.asStateFlow()

    val countUpListener: (Int) -> (Unit) = {
        Timber.d("count up event[$it]")
        _itemCount.value = it+1
        setCartCost(it+1)
    }
    val countDownListener: (Int) -> (Unit) = {
        Timber.d("count down event[$it]")
        if(_itemCount.value != 1) {
            _itemCount.value = it - 1
            setCartCost(it-1)
        }
    }
    val insertButtonClick: (BaseBanchan, Int) -> (Unit) = { banchan, count ->
        insertListener(banchan, count)
        this.dismiss()
    }

    private fun setCartCost(count: Int){
        _cartCost.value = when(banchan.salePrice){
            0L -> banchan.price * count
            else -> banchan.salePrice * count
        }
    }

    val cancelListener: ()->(Unit) = {
        Timber.d("cancel event")
        this.dismiss()
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
        binding.banchan = banchan
        binding.dialog = this
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}