package com.sixbeeps.uniquity;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView; // Added import

import com.sixbeeps.uniquity.data.AppDatabase;
import com.sixbeeps.uniquity.data.UnicodeCharacter;
import com.sixbeeps.uniquity.data.UnicodeGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * The main view for the Uniquity Keyboard
 */
public class UniquityKeyboardView extends LinearLayout {
    /**
     * An interface with methods to be implemented for keyboard events
     */
    public interface UniquityKeyboardListener {
        /**
         * Called when a key is pressed
         * @param contents The characters to be typed in the input field
         */
        void onKey(String contents);

        /**
         * Called when the backspace/delete key is pressed
         */
        void onDelete();
    }

    /**
     * A class to handle incoming click events
     */
    private static class UniquityKeyboardClickListener implements OnClickListener {
        private final UniquityKeyboardListener listener;
        private final UniquityKey key;

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

    private LinearLayout commandStripLayout;
    private ScrollView keysScrollView;
    private LinearLayout rootKeysContainer;

    private static final int KEYBOARD_HEIGHT_DP = 200;
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

    /**
     * Initialize the UniquityKeyboardView
     */
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


        // Query all known characters from the database and add them to the keys list
        List<UnicodeGroup> groups = AppDatabase.INSTANCE.unicodeDao().getInstalledUnicodeGroups();
        for (UnicodeGroup group : groups) {
            List<UnicodeCharacter> characters = AppDatabase.INSTANCE.unicodeDao().getUnicodeCharacters(group.name);
            for (UnicodeCharacter character : characters) {
                keys.add(new UniquityKey(character.character));
            }
        }

        refreshCommandStrip();
        refreshKeysLayout();
    }

    private void refreshCommandStrip() {
        commandStripLayout.removeAllViews();
        Context context = getContext();

        if (keys.isEmpty()) {
            TextView noKeysTextView = new TextView(context);
            noKeysTextView.setText("No characters found. Add character sets via Uniquity settings.");
            noKeysTextView.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
            
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // Add some padding to make it look nicer
            int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()
            );
            noKeysTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            noKeysTextView.setLayoutParams(tvParams);
            noKeysTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); // Requires API 17+
            // Alternatively, for broader compatibility for centering:
            // noKeysTextView.setGravity(Gravity.CENTER); // Would need import android.view.Gravity

            commandStripLayout.addView(noKeysTextView);
        } else {
            // Add DELETE key to command strip
            UniquityKey deleteKey = new UniquityKey(UniquityKey.KeyType.DELETE);
            Button deleteButton = new Button(context);
            deleteButton.setText(deleteKey.getDisplayString());
            deleteButton.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
            deleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            deleteButton.setLayoutParams(params);

            if (this.listener != null) {
                deleteButton.setOnClickListener(new UniquityKeyboardClickListener(this.listener, deleteKey));
            }
            commandStripLayout.addView(deleteButton);

            // Add other command buttons here in the future
        }
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
            currentRow.addView(button);
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
}
