package com.sixbeeps.uniquity;

import android.content.Context;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    private LinearLayout tabStripContentLayout;
    private LinearLayout rootKeysContainer;
    private LinearLayout commandStripLayout;

    private List<UnicodeGroup> allUnicodeGroups = new ArrayList<>();
    private UnicodeGroup currentSelectedGroup;

    private static final int KEYBOARD_HEIGHT_DP = 200;

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
        setOrientation(LinearLayout.VERTICAL);

        int fixedHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, KEYBOARD_HEIGHT_DP, getResources().getDisplayMetrics());

        // Tab Strip
        HorizontalScrollView tabStripScrollView = new HorizontalScrollView(context);
        tabStripScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tabStripContentLayout = new LinearLayout(context);
        tabStripContentLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabStripContentLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        tabStripScrollView.addView(tabStripContentLayout);
        addView(tabStripScrollView);

        // Keybed ScrollView
        ScrollView keysScrollView = new ScrollView(context);
        LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                fixedHeightInPx
        );
        keysScrollView.setLayoutParams(scrollViewParams);
        addView(keysScrollView);

        // Keybed Layout (inside ScrollView)
        rootKeysContainer = new LinearLayout(context);
        rootKeysContainer.setOrientation(LinearLayout.VERTICAL);
        rootKeysContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        keysScrollView.addView(rootKeysContainer);

        // Separator
        View separator = new View(context);
        int separatorHeightPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                separatorHeightPx
        );
        separator.setLayoutParams(separatorParams);
        separator.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_separator_color));
        addView(separator);

        // Command Strip
        commandStripLayout = new LinearLayout(context);
        commandStripLayout.setOrientation(LinearLayout.HORIZONTAL);
        commandStripLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        commandStripLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_command_strip_background));
        addView(commandStripLayout);

        // Load Unicode groups from the database and store them for later
        allUnicodeGroups.clear();
        List<UnicodeGroup> groupsFromDb = AppDatabase.INSTANCE.unicodeDao().getInstalledUnicodeGroups();
        if (groupsFromDb != null) {
            allUnicodeGroups.addAll(groupsFromDb);
        }

        if (!allUnicodeGroups.isEmpty()) {
            currentSelectedGroup = allUnicodeGroups.get(0); // Select the first group by default
        } else {
            currentSelectedGroup = null;
        }

        // Draw all the UI elements
        refreshTabStrip();
        updateKeysForSelectedGroup();
    }

    /**
     * Refreshes the tab strip with Unicode group names.
     */
    private void refreshTabStrip() {
        tabStripContentLayout.removeAllViews();
        Context context = getContext();

        // If there are no groups, display a message
        if (allUnicodeGroups.isEmpty()) {
            TextView noGroupsTextView = new TextView(context);
            noGroupsTextView.setText(R.string.warning_no_group);
            noGroupsTextView.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()
            );
            noGroupsTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            noGroupsTextView.setLayoutParams(tvParams);
            tabStripContentLayout.addView(noGroupsTextView);
            return;
        }

        // Otherwise, create tabs for each group
        for (final UnicodeGroup group : allUnicodeGroups) {
            Button tabButton = new Button(context);
            tabButton.setText(group.name.replaceAll("_", " "));
            tabButton.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
            if (group.equals(currentSelectedGroup)) {
                tabButton.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));
            } else {
                tabButton.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_command_strip_background));
            }

            LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

            int marginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()
            );
            tabParams.setMargins(marginPx, 0, marginPx, 0);
            tabButton.setLayoutParams(tabParams);

            int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()
            );
            tabButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            tabButton.setOnClickListener(v -> {
                currentSelectedGroup = group;
                updateKeysForSelectedGroup();

                // Reset the background color of each tab and highlight the selected one
                for (View button : tabStripContentLayout.getTouchables()) {
                    button.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_command_strip_background));
                }
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));
            });
            tabStripContentLayout.addView(tabButton);
        }
    }

    /**
     * Updates the main key grid with characters from the currently selected Unicode group.
     */
    private void updateKeysForSelectedGroup() {
        keys.clear();
        if (currentSelectedGroup != null) {
            // Get all characters from the selected group
            List<UnicodeCharacter> characters = AppDatabase.INSTANCE.unicodeDao().getUnicodeCharacters(currentSelectedGroup.name);

            // If there are characters, create a key for each of them
            if (characters != null) {
                for (UnicodeCharacter character : characters) {
                    int scalar = Integer.parseInt(character.codepoint, 16);

                    // Handle surrogate pairs if necessary
                    String text;
                    if (scalar > 0xFFFF) {
                        int high = (scalar - 0x10000) / 0x400 + 0xD800;
                        int low = (scalar - 0x10000) % 0x400 + 0xDC00;
                        text = new String(Character.toChars(high)) + new String(Character.toChars(low));
                    } else {
                        text = new String(Character.toChars(scalar));
                    }

                    UniquityKey key = new UniquityKey(text, text, character.codepoint);
                    keys.add(key);
                }
            }
        }

        // Refresh the UI
        refreshKeysLayout();
        refreshCommandStrip();
    }


    /**
     * Refreshes the command strip
     */
    private void refreshCommandStrip() {
        commandStripLayout.removeAllViews();
        Context context = getContext();

        int marginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()
        );

        // Add space bar
        UniquityKey spaceKey = new UniquityKey(" ");
        Button spaceButton = new Button(context);
        spaceButton.setText("␣");
        spaceButton.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
        spaceButton.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));

        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2.0f
        );
        spaceParams.setMargins(marginPx, marginPx, marginPx, marginPx);
        spaceButton.setLayoutParams(spaceParams);

        if (this.listener != null) {
            spaceButton.setOnClickListener(new UniquityKeyboardClickListener(this.listener, spaceKey));
        }
        commandStripLayout.addView(spaceButton);

        // Add delete key
        UniquityKey deleteKey = new UniquityKey(UniquityKey.KeyType.DELETE);
        Button deleteButton = new Button(context);
        deleteButton.setText(deleteKey.getDisplayString()); // Should be "DELETE" or an icon
        deleteButton.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
        deleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));

        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        deleteParams.setMargins(marginPx, marginPx, marginPx, marginPx);
        deleteButton.setLayoutParams(deleteParams);

        if (this.listener != null) {
            deleteButton.setOnClickListener(new UniquityKeyboardClickListener(this.listener, deleteKey));
        }
        commandStripLayout.addView(deleteButton);
    }

    /**
     * Refreshes the layout of the main character keys.
     */
    public void refreshKeysLayout() {
        rootKeysContainer.removeAllViews();
        Context context = getContext();
        LinearLayout currentRow = null;
        final int KEYS_PER_ROW = 8;

        if (keys.isEmpty() && currentSelectedGroup != null) {
            // Display message if selected group has no characters
            TextView noCharsInGroupTextView = new TextView(context);
            noCharsInGroupTextView.setText(R.string.warning_no_char_in_group);
            noCharsInGroupTextView.setTextColor(ContextCompat.getColor(context,R.color.uniquity_button_text_color));
             LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()
            );
            noCharsInGroupTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            noCharsInGroupTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noCharsInGroupTextView.setLayoutParams(tvParams);
            rootKeysContainer.addView(noCharsInGroupTextView);
            rootKeysContainer.requestLayout();
            return;
        }


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
            if (key.getCaption() != null && !key.getCaption().isEmpty()) {
                button.setText(Html.fromHtml(key.getDisplayString() + "<br /><small><small><font color=\"gray\">" + key.getCaption() + "</font></small></small>"));
            } else {
                button.setText(key.getDisplayString());
            }

            button.setTextColor(ContextCompat.getColor(context, R.color.uniquity_button_text_color));
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.uniquity_button_background));

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
        rootKeysContainer.requestLayout(); 
    }

    /**
     * Sets the listener for keyboard events.
     * @param listener The listener to be notified of keyboard events.
     */
    public void setUniquityKeyboardListener(UniquityKeyboardListener listener) {
        this.listener = listener;
        refreshTabStrip();
        updateKeysForSelectedGroup();
    }
}
