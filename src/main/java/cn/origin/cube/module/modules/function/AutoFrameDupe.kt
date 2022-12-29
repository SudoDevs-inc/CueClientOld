package cn.origin.cube.module.modules.function

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.BooleanSetting
import cn.origin.cube.settings.IntegerSetting
import net.minecraft.entity.item.EntityItemFrame
import net.minecraft.init.Items
import net.minecraft.item.ItemShulkerBox
import net.minecraft.item.Item
import net.minecraft.util.EnumHand

@ModuleInfo(name = "AutoFrameDupe",
    descriptions = "AutoFrameDupe",
    category = Category.FUNCTION)
class AutoFrameDupe: Module() {
    private val shulkersonly: BooleanSetting = registerSetting("ShulkersOnly", true)
    private val range: IntegerSetting = registerSetting("Range", 5, 0, 6)
    private val turns: IntegerSetting = registerSetting("Turns", 1, 0, 5)
    private val ticks: IntegerSetting = registerSetting("Ticks", 10, 1, 20)
    private var timeoutTicks = 0

    override fun onUpdate() {
        if (mc.player != null && mc.world != null) {
            if (shulkersonly.value) {
                val shulkerSlot = getShulkerSlot()
                if (shulkerSlot != -1) {
                    mc.player.inventory.currentItem = shulkerSlot
                }
            }
            for (frame in mc.world.loadedEntityList) {
                if (frame is EntityItemFrame) {
                    if (mc.player.getDistance(frame) <= range.value) {
                        if (timeoutTicks >= ticks.value) {
                            if ((frame as EntityItemFrame).displayedItem.item === Items.AIR && !mc.player.getHeldItemMainhand().isEmpty) {
                                mc.playerController.interactWithEntity(mc.player, frame, EnumHand.MAIN_HAND)
                            }
                            if ((frame as EntityItemFrame).displayedItem.item !== Items.AIR) {
                                for (i in 0 until turns.value) {
                                    mc.playerController.interactWithEntity(
                                        mc.player,
                                        frame,
                                        EnumHand.MAIN_HAND
                                    )
                                }
                                mc.playerController.attackEntity(mc.player, frame)
                                timeoutTicks = 0
                            }
                        }
                        ++timeoutTicks
                    }
                }
            }
        }
    }

    private fun getShulkerSlot(): Int {
        var shulkerSlot = -1
        for (i in 0..8) {
            val item: Item = mc.player.inventory.getStackInSlot(i).getItem()
            if (item is ItemShulkerBox) shulkerSlot = i
        }
        return shulkerSlot
    }
}