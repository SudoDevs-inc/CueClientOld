package cn.origin.cube.module.modules.visual;

import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.settings.ModeSetting;
import cn.origin.cube.utils.client.MathUtil;
import cn.origin.cube.utils.render.shader.*;
import cn.origin.cube.utils.render.shader.shaders.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

@ModuleInfo(name = "ShaderCharms",descriptions = "",
        category = Category.VISUAL)
public class ShaderCharms  extends Module {
    private final IntegerSetting range = registerSetting("Range", 32, 8, 64);
    public final ModeSetting<ShaderModes> mode = registerSetting("Mode", ShaderModes.SMOKE);
    private final BooleanSetting crystals = registerSetting("Crystals", true);
    private final BooleanSetting players = registerSetting("Players", false);
    private final BooleanSetting friends = registerSetting("Friends", true);
    private final BooleanSetting mobs = registerSetting("Mobs", false);
    private final BooleanSetting animals = registerSetting("Animals", false);
    private final BooleanSetting enderPearls = registerSetting("Ender Pearls", false);
    private final BooleanSetting itemsEntity = registerSetting("Items(Entity)", false);
    public final BooleanSetting items = registerSetting("Items", true);
    private final BooleanSetting itemsFix = registerSetting("Items Fix", false);

    private final IntegerSetting animationSpeed = registerSetting("Animation Speed", 0, 1, 10);

    private final BooleanSetting blur = registerSetting("Blur", true);
    private final FloatSetting radius = registerSetting("Radius", 2f, 0.1f, 10);
    private final FloatSetting mix = registerSetting("Mix", 1f, 0, 1);
    private final FloatSetting red = registerSetting("Red", 1f, 0, 1);
    private final FloatSetting green = registerSetting("Green", 1f, 0, 1);
    private final FloatSetting blue = registerSetting("Blue", 1f, 0, 1);
    private final IntegerSetting quality = registerSetting("Quality",1, 0, 20);
    private final BooleanSetting gradientAlpha = registerSetting("Gradient Alpha", true);
    private final IntegerSetting alphaGradient = registerSetting("Alpha Gradient Value",255, 0, 255);
    private final FloatSetting duplicateOutline = registerSetting("Duplicate Outline",1f, 0, 20);
    private final FloatSetting moreGradientOutline = registerSetting("More Gradient",1f, 0, 10);
    private final FloatSetting creepyOutline = registerSetting("Creepy",1f, 0, 20);
    private final FloatSetting alpha = registerSetting("Alpha", 1f, 0, 1);
    private final IntegerSetting numOctavesOutline = registerSetting("Num Octaves",1, 1, 30);
    private final FloatSetting speedOutline = registerSetting("Speed", 0f, 0.001f, 0.1f);

    public static ShaderCharms instance;

    private boolean criticalSection = false;

    public ShaderCharms() {
        instance = this;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(items.getValue() && itemsFix.getValue() && (!criticalSection)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        try {
            {
                FramebufferShader framebufferShader = null;
                boolean itemglow = false, gradient = false, glow = false, outline = false;

                switch (mode.getValue().name()) {
                    case "AQUA":
                        framebufferShader = AquaShader.AQUA_SHADER;
                        break;
                    case "RED":
                        framebufferShader = RedShader.RED_SHADER;
                        break;
                    case "SMOKE":
                        framebufferShader = SmokeShader.SMOKE_SHADER;
                        break;
                    case "FLOW":
                        framebufferShader = FlowShader.FLOW_SHADER;
                        break;
                    case "ITEMGLOW":
                        framebufferShader = ItemShader.ITEM_SHADER;
                        itemglow = true;
                        break;
                    case "PURPLE":
                        framebufferShader = PurpleShader.PURPLE_SHADER;
                        break;
                    case "GRADIENT":
                        framebufferShader = GradientOutlineShader.INSTANCE;
                        gradient = true;
                        break;
                    case "UNU":
                        framebufferShader = UnuShader.UNU_SHADER;
                        break;
                    case "GLOW":
                        framebufferShader = GlowShader.GLOW_SHADER;
                        glow = true;
                        break;
                    case "OUTLINE":
                        framebufferShader = OutlineShader.OUTLINE_SHADER;
                        outline = true;
                        break;
                    case "BlueFlames":
                        framebufferShader = BlueFlamesShader.BlueFlames_SHADER;
                        break;
                    case "CodeX":
                        framebufferShader = CodeXShader.CodeX_SHADER;
                        break;
                    case "Crazy":
                        framebufferShader = CrazyShader.CRAZY_SHADER;
                        break;
                    case "Golden":
                        framebufferShader = GoldenShader.GOLDEN_SHADER;
                        break;
                    case "HideF":
                        framebufferShader = HideFShader.HideF_SHADER;
                        break;
                    case "HolyFuck":
                        framebufferShader = HolyFuckShader.HolyFuckF_SHADER;
                        break;
                    case "HotShit":
                        framebufferShader = HotShitShader.HotShit_SHADER;
                        break;
                    case "Kfc":
                        framebufferShader = KfcShader.KFC_SHADER;
                        break;
                    case "Sheldon":
                        framebufferShader = SheldonShader.SHELDON_SHADER;
                        break;
                    case "Smoky":
                        framebufferShader = SmokyShader.SMOKY_SHADER;
                        break;
                    case "SNOW":
                        framebufferShader = SnowShader.SNOW_SHADER;
                        break;
                    case "Techno":
                        framebufferShader = TechnoShader.TECHNO_SHADER;
                        break;
                }

                if (framebufferShader == null) return;

                framebufferShader.animationSpeed = animationSpeed.getValue();

                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.pushMatrix();
                if (itemglow) {
                    ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((ItemShader) framebufferShader).radius = radius.getValue();
                    ((ItemShader) framebufferShader).quality = quality.getValue();
                    ((ItemShader) framebufferShader).blur = blur.getValue();
                    ((ItemShader) framebufferShader).mix = mix.getValue();
                    ((ItemShader) framebufferShader).alpha = 1f;
                    ((ItemShader) framebufferShader).useImage = false;
                } else if (gradient) {
                    ((GradientOutlineShader) framebufferShader).color = getColor();
                    ((GradientOutlineShader) framebufferShader).radius = radius.getValue();
                    ((GradientOutlineShader) framebufferShader).quality = quality.getValue();
                    ((GradientOutlineShader) framebufferShader).gradientAlpha = gradientAlpha.getValue();
                    ((GradientOutlineShader) framebufferShader).alphaOutline = alphaGradient.getValue();
                    ((GradientOutlineShader) framebufferShader).duplicate = duplicateOutline.getValue();
                    ((GradientOutlineShader) framebufferShader).moreGradient = moreGradientOutline.getValue();
                    ((GradientOutlineShader) framebufferShader).creepy = creepyOutline.getValue();
                    ((GradientOutlineShader) framebufferShader).alpha = alpha.getValue();
                    ((GradientOutlineShader) framebufferShader).numOctaves = numOctavesOutline.getValue();
                } else if (glow) {
                    ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((GlowShader) framebufferShader).radius = radius.getValue();
                    ((GlowShader) framebufferShader).quality = quality.getValue();
                } else if (outline) {
                    ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((OutlineShader) framebufferShader).radius = radius.getValue();
                    ((OutlineShader) framebufferShader).quality = quality.getValue();
                }
                framebufferShader.startDraw(event.getPartialTicks());
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity == mc.player || entity == mc.getRenderViewEntity()) continue;
                    if (!((entity instanceof EntityPlayer && players.getValue())
                            || (entity instanceof EntityEnderCrystal && crystals.getValue())
                            || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValue())
                            || ((entity instanceof EntityEnderPearl) && enderPearls.getValue())
                            || ((entity instanceof EntityItem) && itemsEntity.getValue())
                            || (entity instanceof EntityAnimal && animals.getValue()))) continue;
                    Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                    Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                }
                framebufferShader.stopDraw();
                if (gradient) ((GradientOutlineShader) framebufferShader).update(speedOutline.getValue());
                GlStateManager.color(1f, 1f, 1f);
                GlStateManager.matrixMode(5889);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.popMatrix();
            }

            if (items.getValue() && mc.gameSettings.thirdPersonView == 0) {
                FramebufferShader framebufferShader = null;
                boolean itemglow = false, gradient = false, glow = false, outline = false;
                switch (mode.getValue().name()) {
                    case "AQUA":
                        framebufferShader = AquaShader.AQUA_SHADER;
                        break;
                    case "RED":
                        framebufferShader = RedShader.RED_SHADER;
                        break;
                    case "SMOKE":
                        framebufferShader = SmokeShader.SMOKE_SHADER;
                        break;
                    case "FLOW":
                        framebufferShader = FlowShader.FLOW_SHADER;
                        break;
                    case "ITEMGLOW":
                        framebufferShader = ItemShader.ITEM_SHADER;
                        itemglow = true;
                        break;
                    case "PURPLE":
                        framebufferShader = PurpleShader.PURPLE_SHADER;
                        break;
                    case "GRADIENT":
                        framebufferShader = GradientOutlineShader.INSTANCE;
                        gradient = true;
                        break;
                    case "GLOW":
                        framebufferShader = GlowShader.GLOW_SHADER;
                        glow = true;
                        break;
                    case "OUTLINE":
                        framebufferShader = OutlineShader.OUTLINE_SHADER;
                        outline = true;
                        break;
                    case "BlueFlames":
                        framebufferShader = BlueFlamesShader.BlueFlames_SHADER;
                        break;
                    case "CodeX":
                        framebufferShader = CodeXShader.CodeX_SHADER;
                        break;
                    case "Crazy":
                        framebufferShader = CrazyShader.CRAZY_SHADER;
                        break;
                    case "Golden":
                        framebufferShader = GoldenShader.GOLDEN_SHADER;
                        break;
                    case "HideF":
                        framebufferShader = HideFShader.HideF_SHADER;
                        break;
                    case "HolyFuck":
                        framebufferShader = HolyFuckShader.HolyFuckF_SHADER;
                        break;
                    case "HotShit":
                        framebufferShader = HotShitShader.HotShit_SHADER;
                        break;
                    case "Kfc":
                        framebufferShader = KfcShader.KFC_SHADER;
                        break;
                    case "Sheldon":
                        framebufferShader = SheldonShader.SHELDON_SHADER;
                        break;
                    case "Smoky":
                        framebufferShader = SmokyShader.SMOKY_SHADER;
                        break;
                    case "SNOW":
                        framebufferShader = SnowShader.SNOW_SHADER;
                        break;
                    case "Techno":
                        framebufferShader = TechnoShader.TECHNO_SHADER;
                        break;
                }

                if (framebufferShader == null) return;
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableAlpha();
                if (itemglow) {
                    ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((ItemShader) framebufferShader).radius = radius.getValue();
                    ((ItemShader) framebufferShader).quality = 1;
                    ((ItemShader) framebufferShader).blur = blur.getValue();
                    ((ItemShader) framebufferShader).mix = mix.getValue();
                    ((ItemShader) framebufferShader).alpha = 1f;
                    ((ItemShader) framebufferShader).useImage = false;
                } else if (gradient) {
                    ((GradientOutlineShader) framebufferShader).color = getColor();
                    ((GradientOutlineShader) framebufferShader).radius = radius.getValue();
                    ((GradientOutlineShader) framebufferShader).quality = quality.getValue();
                    ((GradientOutlineShader) framebufferShader).gradientAlpha = gradientAlpha.getValue();
                    ((GradientOutlineShader) framebufferShader).alphaOutline = alphaGradient.getValue();
                    ((GradientOutlineShader) framebufferShader).duplicate = duplicateOutline.getValue();
                    ((GradientOutlineShader) framebufferShader).moreGradient = moreGradientOutline.getValue();
                    ((GradientOutlineShader) framebufferShader).creepy = creepyOutline.getValue();
                    ((GradientOutlineShader) framebufferShader).alpha = alpha.getValue();
                    ((GradientOutlineShader) framebufferShader).numOctaves = numOctavesOutline.getValue();
                } else if (glow) {
                    ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((GlowShader) framebufferShader).radius = radius.getValue();
                    ((GlowShader) framebufferShader).quality = quality.getValue();
                } else if (outline) {
                    ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((OutlineShader) framebufferShader).radius = radius.getValue();
                    ((OutlineShader) framebufferShader).quality = quality.getValue();
                }
                criticalSection = true;
                framebufferShader.startDraw(event.getPartialTicks());
                mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
                framebufferShader.stopDraw();
                criticalSection = false;
                if (gradient) ((GradientOutlineShader) framebufferShader).update(speedOutline.getValue());
                GlStateManager.disableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.disableDepth();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        } catch (Exception ignored) {
        }
    }

    public Color getColor() {
        return new Color(red.getValue(), green.getValue(), blue.getValue());
    }

    public enum ShaderModes {
        AQUA, RED, SMOKE, FLOW, ITEMGLOW, PURPLE, GRADIENT, UNU, GLOW, OUTLINE, BlueFlames, CodeX, Crazy, Golden, HideF, HotShit, Kfc, Sheldon, Smoky, SNOW, Techno
    }
}
