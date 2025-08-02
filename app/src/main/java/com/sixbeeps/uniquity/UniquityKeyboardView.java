package com.sixbeeps.uniquity;

import android.content.Context;
// import android.graphics.Color; // Will be replaced by ContextCompat
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class UniquityKeyboardView extends LinearLayout { // Changed from ScrollView
    public interface UniquityKeyboardListener {
        void onKey(String contents);
        void onDelete();
    }

    private class UniquityKeyboardClickListener implements OnClickListener {
        private UniquityKeyboardListener listener;
        private UniquityKey key;

        public UniquityKeyboardClickListener(UniquityKeyboardListener listener, UniquityKey key) {
            this.listener = listener;
            this.key = key;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                UniquityKey.KeyType type = key.getType();
                if (type == UniquityKey.KeyType.DELETE) {
                    listener.onDelete();
                } else if (type == UniquityKey.KeyType.NORMAL) {
                    String contents = key.getContents();
                    if (contents != null && !contents.isEmpty()) {
                        listener.onKey(key.getContents());
                    }
                }
            }
        }
    }

    public List<UniquityKey> keys = new ArrayList<>();
    public UniquityKeyboardListener listener;

    private LinearLayout commandStripLayout; // For non-scrolling command buttons
    private ScrollView keysScrollView;       // For scrolling keys
    private LinearLayout rootKeysContainer;  // Holds the grid of keys inside keysScrollView

    private static final int KEYBOARD_HEIGHT_DP = 200; // Fixed height for the scrolling key area
    private int fixedHeightInPx;

    public UniquityKeyboardView(Context context) {
        this(context, null);
    }

    public UniquityKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UniquityKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL); // Main container is vertical

        fixedHeightInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, KEYBOARD_HEIGHT_DP, getResources().getDisplayMetrics());

        // 1. ScrollView for Keys (now added first)
        keysScrollView = new ScrollView(context);
        LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                fixedHeightInPx // Fixed height for the scroll view
        );
        keysScrollView.setLayoutParams(scrollViewParams);
        addView(keysScrollView);

        // 2. Container for Key Grid (inside ScrollView)
        rootKeysContainer = new LinearLayout(context);
        rootKeysContainer.setOrientation(LinearLayout.VERTICAL);
        rootKeysContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        keysScrollView.addView(rootKeysContainer);

        // 3. Separator View
        View separator = new View(context);
        int separatorHeightPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                separatorHeightPx
        );
        separator.setLayoutParams(separatorParams);
        separator.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_separator_color)); // Subtle separator color
        addView(separator);

        // 4. Command Strip (now added last)
        commandStripLayout = new LinearLayout(context);
        commandStripLayout.setOrientation(LinearLayout.HORIZONTAL);
        commandStripLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        commandStripLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_command_strip_background)); // Set command strip background
        addView(commandStripLayout);


        // Add some example keys (DELETE key is now in command strip)
        keys.add(new UniquityKey("1"));
        keys.add(new UniquityKey("2"));
        keys.add(new UniquityKey("3"));
        for (int i = 4; i <= 40; i++) {
            keys.add(new UniquityKey(String.valueOf(i)));
        }
        // Removed: keys.add(new UniquityKey(UniquityKey.KeyType.DELETE));

        refreshCommandStrip();
        refreshKeysLayout();
    }

    private void refreshCommandStrip() {
        commandStripLayout.removeAllViews();
        Context context = getContext();

        // Add DELETE key to command strip
        UniquityKey deleteKey = new UniquityKey(UniquityKey.KeyType.DELETE);
        Button deleteButton = new Button(context);
        deleteButton.setText(deleteKey.getDisplayString()); // Should be "DEL" or similar
        deleteButton.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
        deleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background)); // Darker, bluer gray for buttons

        // Adjust layout params for command strip buttons if needed, e.g., to not stretch
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Adjust width as needed
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Add some margin if desired
        // params.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        deleteButton.setLayoutParams(params);


        if (this.listener != null) {
            deleteButton.setOnClickListener(new UniquityKeyboardClickListener(this.listener, deleteKey));
        }
        commandStripLayout.addView(deleteButton);

        // Add other command buttons here in the future
    }

    // Renamed from refreshLayout to avoid confusion
    public void refreshKeysLayout() {
        rootKeysContainer.removeAllViews();
        Context context = getContext();
        LinearLayout currentRow = null;
        final int KEYS_PER_ROW = 4;

        for (int i = 0; i < keys.size(); i++) {
            if (i % KEYS_PER_ROW == 0) {
                currentRow = new LinearLayout(context);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                rootKeysContainer.addView(currentRow);
            }

            UniquityKey key = keys.get(i);
            Button button = new Button(context);
            button.setText(key.getDisplayString());
            button.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background)); // Darker, bluer gray for buttons

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            button.setLayoutParams(params);

            if (this.listener != null) {
                button.setOnClickListener(new UniquityKeyboardClickListener(this.listener, key));
            }
            if (currentRow != null) {
                currentRow.addView(button);
            }
        }
        // requestLayout() and invalidate() on rootKeysContainer or keysScrollView might be needed
        // if changes are not reflected, but usually addView/removeAllViews handles this.
        rootKeysContainer.requestLayout(); 
    }

    public void setUniquityKeyboardListener(UniquityKeyboardListener listener) {
        this.listener = listener;
        refreshCommandStrip(); // Refresh command strip for new listener
        refreshKeysLayout();   // Refresh scrolling keys for new listener
    }

    // Helper for dp to px conversion if margins are added to command buttons
    // private int dpToPx(int dp) {
    //    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    // }
}
