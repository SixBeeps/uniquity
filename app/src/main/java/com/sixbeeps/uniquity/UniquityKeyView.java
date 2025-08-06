package com.sixbeeps.uniquity;

import android.content.Context;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

public class UniquityKeyView extends Button {
    public UniquityKeyView(Context context, UniquityKey key) {
        super(context);

        // Update the button's appearance
        setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
        setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));
        setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));

        // Set the button text if there is key data
        if (key != null) {
            if (key.getCaption() != null && !key.getCaption().isEmpty()) {
                setText(Html.fromHtml(key.getDisplayString() + "<br /><small><small><font color=\"gray\">" + key.getCaption() + "</font></small></small>"));
            } else {
                setText(key.getDisplayString());
            }
        }
    }

    public UniquityKeyView(Context context) {
        this(context, null);
    }
}
