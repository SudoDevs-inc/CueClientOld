package cn.origin.cube.module.modules.world

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.BooleanSetting
import cn.origin.cube.settings.IntegerSetting
import cn.origin.cube.settings.ModeSetting
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock

@ModuleInfo(name = "FastPlace",
    descriptions = "FastPlace",
    category = Category.WORLD)
class FastPlace: Module() {
    private var speed: IntegerSetting = registerSetting("Speed", 4,0, 4)
    private var ghostFix: BooleanSetting = registerSetting("GhostFix", true)
    private var blocks: BooleanSetting = registerSetting("Block", true)
    private var exp: BooleanSetting = registerSetting("Exp", true)
    private var fireworks: BooleanSetting = registerSetting("FireWorks", true)
    private var crystals: BooleanSetting = registerSetting("Crystals", true)

    override fun onUpdate() {
        val holding = mc.player.heldItemMainhand.item
        if (blocks.value && holding is ItemBlock
            || exp.value && holding == Items.EXPERIENCE_BOTTLE
            || fireworks.value && holding == Items.FIREWORKS
            || crystals.value && holding == Items.END_CRYSTAL) {

            mc.rightClickDelayTimer = 4 - speed.value
        }

        if (ghostFix.value) {
            // TODO
        }
        super.onUpdate()
    }
}