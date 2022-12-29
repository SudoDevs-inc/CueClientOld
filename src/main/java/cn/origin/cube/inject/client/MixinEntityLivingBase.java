package cn.origin.cube.inject.client;

import cn.origin.cube.module.modules.visual.Animations;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityLivingBase.class })
public class MixinEntityLivingBase
{
    @Inject(method = { "getArmSwingAnimationEnd" }, at = { @At("HEAD") }, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (Animations.INSTANCE.isEnabled() && Animations.INSTANCE.changeSwing.getValue()) {
            info.setReturnValue((int) Animations.INSTANCE.swingDelay.getValue());
        }
    }
}

