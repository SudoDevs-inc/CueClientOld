package cn.origin.cube.module.modules.visual

import cn.origin.cube.event.events.render.*
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import net.minecraftforge.client.event.RenderBlockOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


@ModuleInfo(name = "NoRender",
descriptions = "No",
category = Category.VISUAL)
class NoRender: Module() {

    @SubscribeEvent
    fun onRenderBossOverlay(event: BossOverlayEvent) {
        event.setCanceled(true)
    }

    @SubscribeEvent
    fun onRenderMap(event: RenderMapEvent) {
            event.setCanceled(true)
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent) {
            if (event.overlayType.equals(RenderBlockOverlayEvent.OverlayType.FIRE)) {
                event.isCanceled = true
            }
            if (event.overlayType.equals(RenderBlockOverlayEvent.OverlayType.WATER)) {
                event.isCanceled = true
            }
            if (event.overlayType.equals(RenderBlockOverlayEvent.OverlayType.BLOCK)) {
                event.isCanceled = true
            }
    }

    @SubscribeEvent
    fun onHurtCamera(event: HurtCameraEvent) {
            event.setCanceled(true)
    }

    @SubscribeEvent
    fun onRenderItemActivation(event: RenderItemActivationEvent) {
            event.setCanceled(true)
    }
}