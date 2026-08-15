package com.susen36.caerulaarbor.util;

public enum RecordColor {
    PARCHMENT(0xE8D6A0, 0xB06C38, 0xB06C38),
    DEEPBLUE(0x1b5fda, 0x1d63d7, 0x1d63d7);

    private final int mainColor;
    private final int subBgColor;
    private final int subIconColor;

    RecordColor(int mainColor, int subBgColor, int subIconColor) {
        this.mainColor = mainColor;
        this.subBgColor = subBgColor;
        this.subIconColor = subIconColor;
    }

    public int getMainColor() {
        return mainColor;
    }

    public int getSubBgColor() {
        return subBgColor;
    }

    public int getSubIconColor() {
        return subIconColor;
    }

    public static RecordColor fromName(String name) {
        for (RecordColor value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return PARCHMENT;
    }
}