package cn.origin.cube.module;

import cn.origin.cube.utils.IconFontKt;

import java.awt.*;

public enum Category {
    COMBAT("Combat", IconFontKt.TARGET, false, new Color(213,100,100)),
    MOVEMENT("Movement", IconFontKt.METER, false, new Color(61,29,158)),
    VISUAL("Visual", IconFontKt.EYE, false, new Color(158,28,158)),
    WORLD("World", IconFontKt.EARTH, false, new Color(222, 113, 0)),
    FUNCTION("Function", IconFontKt.COGS, false, new Color(43,169,102)),
    CLIENT("Client", IconFontKt.EQUALIZER, false, new Color(182, 0, 33)),
    HUD("Hud", IconFontKt.PENCLI, true, new Color(255,35,35));

    private final String name;
    private final String icon;
    private final Color color;
    public final boolean isHud;

    Category(String name, String icon, boolean isHud, Color color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.isHud = isHud;

    }

    public String getName() {
        return name;
    }

    public Color getColor(){return color;}

    public String getIcon() {
        return icon;
    }
}
