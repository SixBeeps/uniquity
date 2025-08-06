package com.sixbeeps.uniquity;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class UniquityKeybedLayout extends ScrollView {
    /**
     * Container for key rows
     */
    protected LinearLayout root;

    /**
     * Primary constructor
     * @param context Application context
     * @param height Height of the keybed, in pixels
     */
    public UniquityKeybedLayout(Context context, int height) {
        super(context);

        // Create a container for the keys
        root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        addView(root);

        // Set the height of the view
        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
        ));
    }

    /**
     * Backwards compatibility constructor, defaults height to 10px
     */
    public UniquityKeybedLayout(Context context) {
        this(context, 10);
    }
}
