package com.sixbeeps.uniquity;

/**
 * A single key on the Uniquity keyboard.
 */
public class UniquityKey {

    /**
     * Which action the key should take when pressed
     */
    public enum KeyType {
        /**
         * A key that enters text into the text field
         */
        NORMAL,

        /**
         * A key that deletes the character before the cursor
         */
        DELETE
    }

    KeyType type;
    String contents;
    String label;

    public UniquityKey(String contents) {
        this.type = KeyType.NORMAL;
        this.contents = contents;
    }

    public UniquityKey(String contents, String label) {
        this.type = KeyType.NORMAL;
        this.contents = contents;
        this.label = label;
    }

    public UniquityKey(KeyType type) {
        this.type = type;
        switch (type) {
            case DELETE:
                this.label = "⌫";
                break;
            default:
                break;
        }
    }

    /**
     * Get the <code>KeyType</code> of this key
     */
    public KeyType getType() {
        return type;
    }

    /**
     * If this key is a normal key, get the text that should be entered
     */
    public String getContents() {
        return contents;
    }

    /**
     * If this key has a label, get it. This should not be used for rendering the key. Instead,
     * use <code>getDisplayString()</code>.
     * @see #getDisplayString()
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the appropriate text to use when rendering this key. Returns <code>label</code> unless
     * this key is a normal key with no label, in which case it returns <code>contents</code>.
     */
    public String getDisplayString() {
        if (type == KeyType.NORMAL) {
            if (label != null) return label;
            return contents;
        } else {
            return label;
        }
    }
}
