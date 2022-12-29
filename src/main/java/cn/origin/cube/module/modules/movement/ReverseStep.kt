package cn.origin.cube.module.modules.movement

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.DoubleSetting
import cn.origin.cube.settings.IntegerSetting

@ModuleInfo(name = "ReverseStep",
    descriptions = "Reverse",
    category = Category.MOVEMENT)
class ReverseStep : Module() {

    private var power: DoubleSetting = registerSetting("Power", 10.0, 1.0, 100.0)

    override fun onUpdate() {
        if (mc.player.onGround && !mc.player.isInWater && !mc.player.isInLava && !mc.player.isOnLadder && !mc.gameSettings.keyBindJump.isKeyDown) {
            mc.player.motionY = -power.value
        }
    }
}