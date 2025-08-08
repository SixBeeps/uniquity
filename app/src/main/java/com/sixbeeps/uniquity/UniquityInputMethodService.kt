package com.sixbeeps.uniquity;

import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.InputConnection;
import android.view.View;

public class UniquityInputMethodService extends InputMethodService implements UniquityKeyboardView.UniquityKeyboardListener {

    @Override
    public View onCreateInputView() {
        UniquityKeyboardView kv = new UniquityKeyboardView(this);
        kv.setUniquityKeyboardListener(this);
        return kv;
    }

    @Override
    public void onKey(String contents) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        ic.commitText(contents, 1);
    }

    @Override
    public void onDelete() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        ic.deleteSurroundingText(1, 0);
    }
}