package cn.origin.cube.inject.client;

import cn.origin.cube.event.events.player.MotionEvent;
import cn.origin.cube.event.events.player.SwingArmEvent;
import cn.origin.cube.event.events.player.UpdateWalkingPlayerEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Inject(method = "onUpdateWalkingPlayer",at = @At("RETURN"))
    private void onUpdateWalkingPlayer(CallbackInfo ci){
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent();
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    public void swingArm(EnumHand hand, CallbackInfo ci) {
        SwingArmEvent event = new SwingArmEvent(hand);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }
}
