package cn.origin.cube.module.huds;

import cn.origin.cube.Cube;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.HudModule;
import cn.origin.cube.module.HudModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.settings.ModeSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@HudModuleInfo(name = "WaterMark", x = 114, y = 114, descriptions = "Show hack name", category = Category.HUD)
public class WaterMark extends HudModule {
    public FloatSetting Scala = registerSetting("Size", 1.0f, 0.0f, 10.0f);
    public ModeSetting<nfd> mode = registerSetting("Mode", nfd.Word);
    public BooleanSetting version = registerSetting("Version", false);

    @Override
    public void onRender2D() {
        GL11.glPushMatrix();
        GL11.glTranslated(this.x, (float) this.y, 0);
        GL11.glScaled((double) this.Scala.getValue(), (double) this.Scala.getValue(), 0.0);
        if(mode.getValue().equals(nfd.Word)) {
            drawFadeString("CueClient", 0, 0);
        }
        if(mode.getValue().equals(nfd.Logo)) {
            Cube.fontManager.IconFont.drawString("t", 0, 0, ClickGui.getCurrentColor().getRGB());
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(this.x, (float) this.y, 0);
        GL11.glScaled((double) 1, (double) 1, 0.0);
        if(version.getValue()) {
            Cube.fontManager.CustomFont.drawString("ver:" + Cube.MOD_VERSION, 0 + width, 0 + height - 1, ClickGui.getCurrentColor().getRGB());
        }
        GL11.glPopMatrix();
        this.width = (int) ((float) Cube.fontManager.CustomFont.getStringWidth("CueClient") * this.Scala.getValue());
        this.height = (int) ((float) Cube.fontManager.CustomFont.getHeight() * this.Scala.getValue());
    }

    private void drawFadeString(int scaledWidth, int y, int i, int i1) {
    }

    public static void drawFadeString(final String s, float x, float y) {
        float updateX = x;

        for (int i = 0; i < s.length(); i++) {
            String str = String.valueOf(s.charAt(i));

            Minecraft mc = Minecraft.getMinecraft();
            double colorOffset = (Math.abs(((System.currentTimeMillis()) / 20D)) / 50) + (50 / (mc.fontRenderer.FONT_HEIGHT + i * 14f + 50D));
            Color color = getGradientOffset1(new Color(0xFFFFFF), new Color(0x0081FF), colorOffset, 160);

            Cube.fontManager.CustomFont.drawString(str, (int) updateX, (int) y, color.hashCode());
            updateX += mc.fontRenderer.getStringWidth(str);
        }
    }

    public static Color getGradientOffset1(final Color color1, final Color color2, double offset, final int alpha) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;

        }
        final double inverse_percent = 1 - offset;
        final int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        final int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        final int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart, alpha);
    }

    public enum nfd{
        Logo,Word
    }
}
