package cn.origin.cube.module.modules.function;

import cn.origin.cube.event.events.client.PacketEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BoatPlace",
descriptions = "BoatPlace",
category = Category.FUNCTION)
public class BoatPlace extends Module {
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            if (mc.player.getHeldItemMainhand().getItem() == Items.BOAT) {
                event.setCanceled(true);
            }
        }
    }
}
