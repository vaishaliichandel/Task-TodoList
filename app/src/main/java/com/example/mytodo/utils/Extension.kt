package com.example.mytodo.utils

import android.view.View
import com.example.mytodo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

fun BottomSheetDialogFragment.setBottomShitBackground() {
    dialog?.apply {
        setOnShowListener {
            val bottomSheet = findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(R.drawable.bg_bottom_shit)
        }
    }
}