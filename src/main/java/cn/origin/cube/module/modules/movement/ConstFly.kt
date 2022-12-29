package cn.origin.cube.module.modules.movement

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo

@ModuleInfo(name = "ConstFly",
    descriptions = "ConstFly",
    category = Category.MOVEMENT)
class ConstFly: Module() {
    var c = 0

    override fun onUpdate() {
        if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(0.0, -0.1, 0.0)).isEmpty()) {
            mc.player.motionY = 0.0
            if (c > 40) {
                mc.player.posY -= 0.032
                c = 0
            } else c++
            if (mc.player.ticksExisted % 3 != 0) mc.player.setPosition(
                mc.player.posX,
                1.0e-9.let { mc.player.posY += it; mc.player.posY },
                mc.player.posZ
            )
        }
        super.onUpdate()
    }

    override fun onEnable() {
        c = 0
    }
}