package cn.origin.cube.module.modules.function

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.BooleanSetting
import cn.origin.cube.settings.FloatSetting
import cn.origin.cube.settings.ModeSetting
import cn.origin.cube.utils.Utils.nullCheck
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

@ModuleInfo(name = "PacketEXP",
    descriptions = "PacketEXP",
    category = Category.FUNCTION)
class PacketEXP: Module() {

    private var triggerMode: ModeSetting<mode> = registerSetting("TriggerMode", mode.RightClick)
    private var packets = FloatSetting("Packets", 2f, 0f, 10f, this)
    private var onlyInHand: BooleanSetting = registerSetting("OnlyInHand", false)

    override fun onUpdate() {
        if (nullCheck()) return
        if (triggerMode.value.equals(mode.RightClick) && !mc.gameSettings.keyBindUseItem.isKeyDown) return
        if (triggerMode.value.equals(mode.MiddleClick) && !Mouse.isButtonDown(2)) return
        if (onlyInHand.value && mc.player.heldItemMainhand.item != Items.EXPERIENCE_BOTTLE) return
        if (getItemHotbar(Items.EXPERIENCE_BOTTLE) === -1) return
        mc.player.connection.sendPacket(CPacketHeldItemChange(getItemHotbar(Items.EXPERIENCE_BOTTLE)))
        for (i in 0 until packets.value.toInt()) {
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
        }
        mc.player.connection.sendPacket(CPacketHeldItemChange(mc.player.inventory.currentItem))
    }

    private fun getItemHotbar(item: Item): Int {
        var itemSlot = -1
        for (i in 9 downTo 1) if (mc.player.inventory.getStackInSlot(i).item == item) {
            itemSlot = i
            break
        }
        return itemSlot
    }

    enum class mode{
        RightClick,MiddleClick
    }
}