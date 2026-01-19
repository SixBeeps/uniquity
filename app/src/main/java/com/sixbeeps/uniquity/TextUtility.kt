package com.sixbeeps.uniquity

class TextUtility {
    companion object {
        fun codepointToString(codepoint: Int): String {
            if (codepoint > 0xFFFF) {
                val high = (codepoint - 0x10000) / 0x400 + 0xD800
                val low = (codepoint - 0x10000) % 0x400 + 0xDC00
                return String(Character.toChars(high)) + String(Character.toChars(low))
            } else {
                return String(Character.toChars(codepoint))
            }
        }
    }
}