package com.sixbeeps.uniquity;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.inputmethod.InputConnection;
import android.view.View;
import android.text.TextUtils;

public class UniquityInputMethodService extends InputMethodService implements UniquityKeyboardView.UniquityKeyboardListener {

    @Override
    public View onCreateInputView() {
        // get the KeyboardView and add our Keyboard layout to it
//        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
//        Keyboard keyboard = new Keyboard(this, R.xml.number_pad);
//        keyboardView.setKeyboard(keyboard);
//        keyboardView.setOnKeyboardActionListener(this);
//        return keyboardView;
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