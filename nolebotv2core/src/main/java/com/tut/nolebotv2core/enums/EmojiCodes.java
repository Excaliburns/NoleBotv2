package com.tut.nolebotv2core.enums;

public enum EmojiCodes {

    // Reaction Emojis
    EXIT("\u274C"), // ❌
    PREVIOUS_ARROW("\u2B05"), // ⬅
    NEXT_ARROW("\u27A1"), // ➡

    // Misc
    DASH("\u2014"), // —
    HEART("\u2764"), // ❤
    PLEADING("\uD83E\uDD7A"), // 🥺
    WARNING_ARROW("\u26A0"), // ⚠
    WAVING_HAND("\uD83D\uDC4B"), // 👋
    THINKING("\uD83E\uDD14"), // 🤔

    // Error / Success
    DOUBLE_BANG("\u203C"), //‼
    CHECK_MARK("\u2705"); //✅

    public final String unicodeValue;

    EmojiCodes(String unicodeValue) {
        this.unicodeValue = unicodeValue;
    }
}
