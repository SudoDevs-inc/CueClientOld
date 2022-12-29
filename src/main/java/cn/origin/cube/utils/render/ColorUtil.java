package cn.origin.cube.utils.render;

import java.awt.*;

public class ColorUtil {
    public static Color interpolate(final float value, final Color start, final Color end) {
        final float sr = start.getRed() / 255.0f;
        final float sg = start.getGreen() / 255.0f;
        final float sb = start.getBlue() / 255.0f;
        final float sa = start.getAlpha() / 255.0f;
        final float er = end.getRed() / 255.0f;
        final float eg = end.getGreen() / 255.0f;
        final float eb = end.getBlue() / 255.0f;
        final float ea = end.getAlpha() / 255.0f;
        final float r = sr * value + er * (1.0f - value);
        final float g = sg * value + eg * (1.0f - value);
        final float b = sb * value + eb * (1.0f - value);
        final float a = sa * value + ea * (1.0f - value);
        return new Color(r, g, b, a);
    }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        return (Color.getHSBColor(hue / speed, s, 1f).getRGB());
    }

    public static int toRGBA(final int r, final int g, final int b) {
        return toRGBA(r, g, b, 255);
    }

    public static int toRGBA(final int r, final int g, final int b, final int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

}

