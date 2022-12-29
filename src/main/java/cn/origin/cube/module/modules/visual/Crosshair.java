package cn.origin.cube.module.modules.visual;

import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.utils.render.Render2DUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@ModuleInfo(name = "Crosshair",
        descriptions = "Crosshair",
        category = Category.VISUAL)
public class Crosshair extends Module {

    FloatSetting length =  registerSetting("Length", 10.0f, 0.0f, 25.0f);
    FloatSetting partWidth = registerSetting("Part Width", 2.5f, 0.0f, 25.0f);
    FloatSetting gap = registerSetting("Gap", 6.1f, 0.0f, 25.0f);
    FloatSetting outlineWidth = registerSetting("Outline Width", 1.0f, 0.0f, 5.0f);
    BooleanSetting dynamic = registerSetting("Dynamic", true);
    BooleanSetting attackIndicator = registerSetting("Attack Indicator", true);
    BooleanSetting fill = registerSetting("Fill", true);
    BooleanSetting outline = registerSetting("Outline", true);

    @Override
    public void onEnable() {
        GuiIngameForge.renderCrosshairs = false;
    }

    @Override
    public void onDisable() {
        GuiIngameForge.renderCrosshairs = true;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event)
    {
        final ScaledResolution resolution = new ScaledResolution(Crosshair.mc);
        final float width = resolution.getScaledWidth() / 2.0f;
        final float height = resolution.getScaledHeight() / 2.0f;
        if (this.fill.getValue()) {
            Render2DUtil.drawRect1(width - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2.0f : 0.0f), height - this.partWidth.getValue() / 2.0f, width - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2.0f : 0.0f) + this.length.getValue(), height - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), ClickGui.getCurrentColor().getRGB());
            Render2DUtil.drawRect1(width + this.gap.getValue() + (this.isMoving() ? 2 : 0), height - this.partWidth.getValue() / 2.0f, width + this.gap.getValue() + (this.isMoving() ? 2 : 0) + this.length.getValue(), height - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), ClickGui.getCurrentColor().getRGB());
            Render2DUtil.drawRect1(width - this.partWidth.getValue() / 2.0f, height - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2 : 0), width - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), height - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2 : 0) + this.length.getValue(), ClickGui.getCurrentColor().getRGB());
            Render2DUtil.drawRect1(width - this.partWidth.getValue() / 2.0f, height + this.gap.getValue() + (this.isMoving() ? 2 : 0), width - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), height + this.gap.getValue() + (this.isMoving() ? 2 : 0) + this.length.getValue(), ClickGui.getCurrentColor().getRGB());
            if (this.attackIndicator.getValue() && Crosshair.mc.player.getCooledAttackStrength(0.0f) < 1.0f) {
                Render2DUtil.drawRect1(width - 10.0f, height + this.gap.getValue() + this.length.getValue() + (this.isMoving() ? 2 : 0) + 2.0f, width - 10.0f + Crosshair.mc.player.getCooledAttackStrength(0.0f) * 20.0f, height + this.gap.getValue() + this.length.getValue() + (this.isMoving() ? 2 : 0) + 2.0f + 2.0f, ClickGui.getCurrentColor().getRGB());
            }
        }
        if (this.outline.getValue()) {
            drawOutline(width - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2.0f : 0.0f), height - this.partWidth.getValue() / 2.0f, width - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2.0f : 0.0f) + this.length.getValue(), height - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), this.outlineWidth.getValue(), new Color(0,0,0).getRGB());
            drawOutline(width + this.gap.getValue() + (this.isMoving() ? 2 : 0), height - this.partWidth.getValue() / 2.0f, width + this.gap.getValue() + (this.isMoving() ? 2 : 0) + this.length.getValue(), height - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), this.outlineWidth.getValue(), new Color(0,0,0).getRGB());
            drawOutline(width - this.partWidth.getValue() / 2.0f, height - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2 : 0), width - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), height - this.gap.getValue() - this.length.getValue() - (this.isMoving() ? 2 : 0) + this.length.getValue(), this.outlineWidth.getValue(), new Color(0,0,0).getRGB());
            drawOutline(width - this.partWidth.getValue() / 2.0f, height + this.gap.getValue() + (this.isMoving() ? 2 : 0), width - this.partWidth.getValue() / 2.0f + this.partWidth.getValue(), height + this.gap.getValue() + (this.isMoving() ? 2 : 0) + this.length.getValue(), this.outlineWidth.getValue(), new Color(0,0,0).getRGB());
            if (this.attackIndicator.getValue() && Crosshair.mc.player.getCooledAttackStrength(0.0f) < 1.0f) {
                drawOutline(width - 10.0f, height + this.gap.getValue() + this.length.getValue() + (this.isMoving() ? 2 : 0) + 2.0f, width - 10.0f + Crosshair.mc.player.getCooledAttackStrength(0.0f) * 20.0f, height + this.gap.getValue() + this.length.getValue() + (this.isMoving() ? 2 : 0) + 2.0f + 2.0f, this.outlineWidth.getValue(), new Color(0,0,0).getRGB());
            }
        }
    }

    public boolean isMoving() {
        return (Crosshair.mc.player.isSneaking() || Crosshair.mc.player.moveStrafing != 0.0f || Crosshair.mc.player.moveForward != 0.0f || !Crosshair.mc.player.onGround) && this.dynamic.getValue();
    }

    public static void drawOutline(float x, float y, float width, float height, float lineWidth, int color) {
        Render2DUtil.drawRect1(x + lineWidth, y, x - lineWidth, y + lineWidth, color);
        Render2DUtil.drawRect1(x + lineWidth, y, width - lineWidth, y + lineWidth, color);
        Render2DUtil.drawRect1(x, y, x + lineWidth, height, color);
        Render2DUtil.drawRect1(width - lineWidth, y, width, height, color);
        Render2DUtil.drawRect1(x + lineWidth, height - lineWidth, width - lineWidth, height, color);
    }
}
