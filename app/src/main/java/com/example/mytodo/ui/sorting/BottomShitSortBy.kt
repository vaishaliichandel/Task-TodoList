package com.example.mytodo.ui.sorting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mytodo.databinding.BottomShitSortByBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomShitSortBy : BottomSheetDialogFragment() {
    private lateinit var binding: BottomShitSortByBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomShitSortByBinding.inflate(inflater, container, false)
        return binding.root
    }
}