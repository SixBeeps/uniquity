package com.sixbeeps.uniquity

/**
 * A single key on the Uniquity keyboard.
 */
class UniquityKey {
    /**
     * Which action the key should take when pressed
     */
    enum class KeyType {
        /**
         * A key that enters text into the text field
         */
        NORMAL,

        /**
         * A key that deletes the character before the cursor
         */
        DELETE,

        /**
         * A key that either performs a submission, or inserts a newline
         */
        ENTER,
    }

    /**
     * Get the `KeyType` of this key
     */
    @JvmField
    var type: KeyType?

    /**
     * If this key is a normal key, get the text that should be entered
     */
    @JvmField
    var contents: String? = null

    /**
     * If this key has a label, get it. This should not be used for rendering the key. Instead,
     * use `getDisplayString()`.
     * @see .getDisplayString
     */
    var label: String? = null

    /**
     * If this key has a caption, get it. For Uniquity, this is the Unicode codepoint.
     */
    @JvmField
    var caption: String? = null

    constructor(contents: String?) {
        this.type = KeyType.NORMAL
        this.contents = contents
    }

    constructor(contents: String?, label: String?) {
        this.type = KeyType.NORMAL
        this.contents = contents
        this.label = label
    }

    constructor(contents: String?, label: String?, caption: String?) {
        this.type = KeyType.NORMAL
        this.contents = contents
        this.label = label
        this.caption = caption
    }

    constructor(type: KeyType) {
        this.type = type
        when (type) {
            KeyType.DELETE -> this.label = "⌫"
            KeyType.ENTER -> this.label = "⏎"
            else -> {}
        }
    }

    val displayString: String?
        /**
         * Get the appropriate text to use when rendering this key. Returns `label` unless
         * this key is a normal key with no label, in which case it returns `contents`.
         */
        get() {
            if (type == KeyType.NORMAL) {
                if (label != null) return label
                return contents
            } else {
                return label
            }
        }
}
