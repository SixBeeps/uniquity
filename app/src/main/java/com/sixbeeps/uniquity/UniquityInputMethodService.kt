package com.sixbeeps.uniquity

import android.inputmethodservice.InputMethodService
import android.view.View

/**
 * Defines Uniquity as an input method service which can be used to manipulate
 * text in the input field.
 */
class UniquityInputMethodService : InputMethodService(), UniquityKeyboardView.UniquityKeyboardListener {
    override fun onCreateInputView(): View {
        val kv = UniquityKeyboardView(this)
        kv.setUniquityKeyboardListener(this)
        return kv
    }

    override fun onKey(contents: String?) {
        val ic = currentInputConnection
        if (ic == null) return

        ic.commitText(contents, 1)
    }

    override fun onDelete() {
        val ic = currentInputConnection
        if (ic == null) return

        ic.deleteSurroundingText(1, 0)
    }
}