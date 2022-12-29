package cn.origin.cube.module.huds;

import cn.origin.cube.Cube;
import cn.origin.cube.module.*;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.ModeSetting;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.Comparator;
import java.util.stream.Collectors;

@HudModuleInfo(name = "ModuleArrayList", descriptions = "Show all enable module", category = Category.HUD, y = 100, x = 100)
public class ModuleArrayList extends HudModule {

    public ModeSetting<?> alignSetting = registerSetting("Align", alignMode.Left);
    public ModeSetting<?> sortSetting = registerSetting("Sort", sortMode.Top);
    public ModeSetting<?> colorSetting = registerSetting("Color", colorMode.Fade);
    public int count = 0;

    @Override
    public void onRender2D() {
        count = 0;
        Cube.moduleManager.getModuleList().stream()
                .filter(AbstractModule::isEnabled)
                .filter(module -> module.visible.getValue())
                .sorted(Comparator.comparing(module -> Cube.fontManager.CustomFont.getStringWidth(module.getFullHud())
                        * (sortSetting.getValue().equals(sortMode.Bottom) ? 1 : -1)))
                .forEach(module -> {
                    float modWidth = Cube.fontManager.CustomFont.getStringWidth(module.getFullHud());
                    String modText = module.getFullHud();
                    Color c;
                    if(colorSetting.getValue().equals(colorMode.CategoryC)){
                        c = module.category.getColor();
                    }
                    if(colorSetting.getValue().equals(colorMode.Fade)){
                        c = ClickGui.getCurrentColor();
                    }
                    else{
                        c = ClickGui.getCurrentColor();
                    }
                    if(colorSetting.getValue().equals(colorMode.Static) || colorSetting.getValue().equals(colorMode.CategoryC)) {
                        if (alignMode.Right.equals(alignSetting.getValue())) {
                            Cube.fontManager.CustomFont.drawStringWithShadow(modText,
                                    (int) (this.x - 2 - modWidth + this.width),
                                    this.y + (10 * count),
                                    c.getRGB());
                        } else {
                            Cube.fontManager.CustomFont.drawStringWithShadow(modText,
                                    this.x - 2,
                                    this.y + (10 * count),
                                    c.getRGB());
                        }
                    }else{
                        if (alignMode.Right.equals(alignSetting.getValue())) {
                            drawFadeString(modText,
                                    (int) (this.x - 2 - modWidth + this.width),
                                    this.y + (10 * count));
                        } else {
                            drawFadeString(modText,
                                    this.x - 2,
                                    this.y + (10 * count));
                        }
                    }
                    count++;
                });
        width = Cube.moduleManager.getModuleList().stream()
                .filter(AbstractModule::isEnabled)
                .noneMatch(module -> module.visible.getValue()) ? 20 :
                Cube.fontManager.CustomFont.getStringWidth(Cube.moduleManager.getModuleList()
                        .stream().filter(AbstractModule::isEnabled)
                        .filter(module -> module.visible.getValue())
                        .sorted(Comparator.comparing(module -> Cube.fontManager.CustomFont.getStringWidth(module.getFullHud()) * (-1)))
                        .collect(Collectors.toList()).get(0).getFullHud());
        height = ((Cube.fontManager.CustomFont.getHeight() + 1) *
                (int) Cube.moduleManager.getModuleList().stream()
                        .filter(AbstractModule::isEnabled).count());
    }

    public static void drawFadeString(final String s, float x, float y) {
        float updateX = x;

        for (int i = 0; i < s.length(); i++) {
            String str = String.valueOf(s.charAt(i));

            Minecraft mc = Minecraft.getMinecraft();
            double colorOffset = (Math.abs(((System.currentTimeMillis()) / 20D)) / 50) + (50 / (mc.fontRenderer.FONT_HEIGHT + i * 14f + 50D));
            Color color = getGradientOffset1(new Color(0xFFFFFF), new Color(0x0081FF), colorOffset, 190);

            mc.fontRenderer.drawString(str, (int) updateX, (int) y, color.hashCode());
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

    enum alignMode {
        Left,
        Right
    }

    enum colorMode {
        CategoryC,Fade,Static
    }

    enum sortMode {
        Top,
        Bottom
    }
}
