package cn.origin.cube.inject.client;

import cn.origin.cube.event.events.render.HurtCameraEvent;
import cn.origin.cube.event.events.render.RenderItemActivationEvent;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    private ItemStack itemActivationItem;

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info) {
        HurtCameraEvent hurtCameraEvent = new HurtCameraEvent();
        MinecraftForge.EVENT_BUS.post(hurtCameraEvent);

        if (hurtCameraEvent.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = "renderItemActivation", at = @At("HEAD"), cancellable = true)
    public void onRenderItemActivation(CallbackInfo info) {
        RenderItemActivationEvent renderItemActivationEvent = new RenderItemActivationEvent();
        MinecraftForge.EVENT_BUS.post(renderItemActivationEvent);

        if (itemActivationItem != null && itemActivationItem.getItem().equals(Items.TOTEM_OF_UNDYING)) {
            if (renderItemActivationEvent.isCanceled()) {
                info.cancel();
            }
        }
    }
}
