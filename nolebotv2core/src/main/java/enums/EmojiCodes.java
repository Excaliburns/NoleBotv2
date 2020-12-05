package enums;

public enum EmojiCodes {

    // Reaciton Emojis
    EXIT("\u274C"), // ❌
    PREVIOUS_ARROW("\u2B05"), // ⬅
    NEXT_ARROW("\u27A1"), // ➡

    // Misc
    HEART("\u2764"), // ❤
    WARNING_ARROW("\u26A0"), // ⚠
    WAVING_HAND("\uD83D\uDC4B"); // 👋

    public final String unicodeValue;

    EmojiCodes(String unicodeValue) {
        this.unicodeValue = unicodeValue;
    }
}
