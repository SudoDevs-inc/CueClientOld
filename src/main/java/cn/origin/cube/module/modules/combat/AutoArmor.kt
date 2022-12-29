package cn.origin.cube.module.modules.combat

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.module.modules.client.AutoConfig
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.InventoryEffectRenderer
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack


@ModuleInfo(name = "AutoArmor",
    descriptions = "AutoArmor",
    category = Category.COMBAT)
class AutoArmor:Module() {

    override fun onUpdate() {
        if (!(mc.player == null || mc.world == null)) {
            if (mc.player.ticksExisted % 2 == 0) return
            if (mc.currentScreen is GuiContainer && mc.currentScreen !is InventoryEffectRenderer) return
            val bestArmorSlots = IntArray(4)
            val bestArmorValues = IntArray(4)
            for (armorType in 0..3) {
                val oldArmor = mc.player.inventory.armorItemInSlot(armorType)
                if (oldArmor.item is ItemArmor) bestArmorValues[armorType] =
                    (oldArmor.item as ItemArmor).damageReduceAmount
                bestArmorSlots[armorType] = -1
            }
            for (slot in 0..35) {
                val stack = mc.player.inventory.getStackInSlot(slot)
                if (stack.count > 1) continue
                if (stack.item !is ItemArmor) continue
                val armor = stack.item as ItemArmor
                val armorType = armor.armorType.ordinal - 2
                if (armorType == 2 && mc.player.inventory.armorItemInSlot(armorType).item == Items.ELYTRA) continue
                val armorValue = armor.damageReduceAmount
                if (armorValue > bestArmorValues[armorType]) {
                    bestArmorSlots[armorType] = slot
                    bestArmorValues[armorType] = armorValue
                }
            }
            for (armorType in 0..3) {
                var slot = bestArmorSlots[armorType]
                if (slot == -1) continue
                val oldArmor = mc.player.inventory.armorItemInSlot(armorType)
                if (oldArmor != ItemStack.EMPTY || mc.player.inventory.firstEmptyStack != -1) {
                    if (slot < 9) slot += 36
                    mc.playerController.windowClick(0, 8 - armorType, 0, ClickType.QUICK_MOVE, mc.player)
                    mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player)
                    break
                }
            }
        }
    }

    var INSTANCE: AutoArmor? = null

    public fun AutoArmor() {
        INSTANCE = this
    }
}