package cn.origin.cube.module.modules.visual;

import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.settings.ModeSetting;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "Animations",
        descriptions = "Animations",
        category = Category.VISUAL)
public class Animations extends Module {

    public ModeSetting<AnimationVersion> swingAnimationVersion = registerSetting("Version", AnimationVersion.OneDotTwelve);
    public BooleanSetting playersDisableAnimations = registerSetting("PlayersAnimations", false);
    public BooleanSetting changeMainhand = registerSetting("ChangeMainhand", false);
    public BooleanSetting changeOffhand = registerSetting("ChangeOffhand", false);
    public BooleanSetting changeSwing = registerSetting("ChangeSwing", false);
    public FloatSetting mainhand = registerSetting("ChangeSwing", Float.intBitsToFloat(Float.floatToIntBits(4.7509747f) ^ 0x7F1807FC), Float.intBitsToFloat(Float.floatToIntBits(1.63819E38f) ^ 0x7EF67CC9), Float.intBitsToFloat(Float.floatToIntBits(30.789412f) ^ 0x7E7650B7));
    public FloatSetting offhand = registerSetting("ChangeSwing", Float.intBitsToFloat(Float.floatToIntBits(15.8065405f) ^ 0x7EFCE797), Float.intBitsToFloat(Float.floatToIntBits(3.3688825E38f) ^ 0x7F7D7251), Float.intBitsToFloat(Float.floatToIntBits(7.3325067f) ^ 0x7F6AA3E5));
    public IntegerSetting swingDelay = registerSetting("ChangeSwing", 5, 1, 30);

    public static Animations INSTANCE;

    public Animations() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (playersDisableAnimations.getValue()) {
            for (final EntityPlayer player : mc.world.playerEntities) {
                player.limbSwing = Float.intBitsToFloat(Float.floatToIntBits(1.8755627E38f) ^ 0x7F0D1A06);
                player.limbSwingAmount = Float.intBitsToFloat(Float.floatToIntBits(6.103741E37f) ^ 0x7E37AD83);
                player.prevLimbSwingAmount = Float.intBitsToFloat(Float.floatToIntBits(4.8253957E37f) ^ 0x7E11357F);
            }
        }
        if (changeMainhand.getValue() && mc.entityRenderer.itemRenderer.equippedProgressMainHand != mainhand.getValue()) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = mainhand.getValue();
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
        if (changeOffhand.getValue() && mc.entityRenderer.itemRenderer.equippedProgressOffHand != offhand.getValue()) {
            mc.entityRenderer.itemRenderer.equippedProgressOffHand = offhand.getValue();
            mc.entityRenderer.itemRenderer.itemStackOffHand = mc.player.getHeldItemOffhand();
        }
        if (swingAnimationVersion.getValue() == AnimationVersion.OneDotEight && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
    }

    public enum AnimationVersion
    {
        OneDotEight,
        OneDotTwelve;
    }

}
