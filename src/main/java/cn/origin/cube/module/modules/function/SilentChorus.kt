package cn.origin.cube.module.modules.function

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.utils.player.InventoryUtil
import net.minecraft.init.Items
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand

@ModuleInfo(name = "SilentChorus",
    descriptions = "SilentChorus",
    category = Category.FUNCTION)
class SilentChorus:Module() {
    var oldSlot = 0

    override fun onEnable() {
        oldSlot = mc.player.inventory.currentItem
    }

    override fun onUpdate() {
        val slot: Int = InventoryUtil.findItemInHotbar(Items.CHORUS_FRUIT)
        if (InventoryUtil.findItemInHotbar(Items.CHORUS_FRUIT) !== -1) {
            mc.player.connection.sendPacket(CPacketHeldItemChange(slot) as Packet<*>)
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND) as Packet<*>)
            toggle()
        }
    }

}