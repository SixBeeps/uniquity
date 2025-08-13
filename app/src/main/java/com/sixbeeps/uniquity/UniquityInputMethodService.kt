package com.sixbeeps.uniquity

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo

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

    override fun onEnter() {
        val ic = currentInputConnection
        if (ic == null) return

        // Check if the enter key should put a newline or submit a go action
        val imeOptions = currentInputEditorInfo.imeOptions
        if (imeOptions and EditorInfo.IME_ACTION_GO != 0) {
            ic.performEditorAction(EditorInfo.IME_ACTION_GO)
        } else {
            ic.commitText("\n", 1)
        }
    }
}