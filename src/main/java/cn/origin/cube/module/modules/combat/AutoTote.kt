package cn.origin.cube.module.modules.combat

import cn.origin.cube.event.events.client.PacketEvent
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.utils.player.InventoryUtil
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name = "AutoTotem",
    descriptions = "A",
    category = Category.COMBAT)
class AutoTote : Module() {

    override fun onUpdate() {
        if (mc.currentScreen is GuiContainer && mc.currentScreen !is GuiInventory) return
        val totemslot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING)
        if (mc.player.heldItemOffhand.item !== Items.TOTEM_OF_UNDYING && totemslot != -1) {
            mc.playerController.windowClick(
                mc.player.inventoryContainer.windowId,
                totemslot,
                0,
                ClickType.PICKUP,
                mc.player
            )
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player)
            mc.playerController.windowClick(
                mc.player.inventoryContainer.windowId,
                totemslot,
                0,
                ClickType.PICKUP,
                mc.player
            )
            mc.playerController.updateController()
        }
        super.onUpdate()
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvent.Send) {
        if (event.getPacket<Packet<*>>() is CPacketClickWindow) {
            mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING))
        }
    }
}