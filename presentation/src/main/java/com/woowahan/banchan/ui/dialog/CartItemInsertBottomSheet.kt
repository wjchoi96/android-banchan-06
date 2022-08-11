package com.woowahan.banchan.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woowahan.banchan.databinding.DialogCartAddBinding
import com.woowahan.banchan.util.toCashString
import com.woowahan.domain.model.BanchanModel
import timber.log.Timber

class CartItemInsertBottomSheet constructor(
    private val banchan: BanchanModel,
    private val insertListener: (BanchanModel, Int) -> (Unit)
): BottomSheetDialogFragment() {
    private var _binding: DialogCartAddBinding? = null
    private val binding get() = _binding ?: error("Binding not Initialized")

    private var _itemCount: MutableLiveData<Int> = MutableLiveData(1)
    val itemCount: LiveData<Int> = _itemCount

    private var _cartCost: MutableLiveData<String> = MutableLiveData(banchan.salePrice ?: banchan.price)
    var cartCost: LiveData<String> = _cartCost

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
    val insertButtonClick: (BanchanModel, Int) -> (Unit) = { banchan, count ->
        insertListener(banchan, count)
        this.dismiss()
    }

    private fun setCartCost(count: Int){
        _cartCost.value = when(banchan.salePriceRaw){
            0L -> (banchan.priceRaw * count).toCashString() + "원"
            else -> (banchan.salePriceRaw * count).toCashString() + "원"
        }
    }

    val cancelListener: ()->(Unit) = {
        Timber.d("cancel event")
        this.dismiss()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}