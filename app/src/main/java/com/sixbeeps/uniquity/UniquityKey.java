package com.sixbeeps.uniquity;

public class UniquityKey {
    public enum KeyType {
        NORMAL,
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

    public KeyType getType() {
        return type;
    }

    public String getContents() {
        return contents;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplayString() {
        if (type == KeyType.NORMAL) {
            if (label != null) return label;
            return contents;
        } else {
            return label;
        }
    }
}
