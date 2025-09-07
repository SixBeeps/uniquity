package com.sixbeeps.uniquity

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo

/**
 * Defines Uniquity as an input method service which can be used to manipulate
 * text in the input field.
 */
class UniquityInputMethodService : InputMethodService(), UniquityKeyboardView.UniquityKeyboardListener {
    private var keyboardView: UniquityKeyboardView? = null
    
    override fun onCreateInputView(): View {
        keyboardView = UniquityKeyboardView(this)
        keyboardView?.setUniquityKeyboardListener(this)
        return keyboardView!!
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
        val actionBits = currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        if (actionBits and EditorInfo.IME_ACTION_GO != 0) {
            ic.performEditorAction(EditorInfo.IME_ACTION_GO)
        } else if (actionBits and EditorInfo.IME_ACTION_SEND != 0) {
            ic.performEditorAction(EditorInfo.IME_ACTION_SEND)
        } else if (actionBits and EditorInfo.IME_ACTION_SEARCH != 0) {
            ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
        } else {
            ic.commitText("", 1)
        }
    }

    override fun onLongPress(codepoint: String?) {
        // Delegate to the keyboard view to handle adding to favorites
        keyboardView?.addToFavorites(codepoint)
    }
}