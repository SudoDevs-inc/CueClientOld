package cn.origin.cube.module.modules.world

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.IntegerSetting
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.MathHelper

@ModuleInfo(name = "Rotator",
    descriptions = "Rotator",
    category = Category.WORLD)
class Rotator: Module() {

    val speed: IntegerSetting = registerSetting("Speed", 5, 0, 15)
    var yaw = 0

    override fun onUpdate() {
        if (mc.world == null || mc.player == null) return

        if (this.isEnabled) {
            yaw += speed.value
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(MathHelper.wrapDegrees(yaw).toFloat(), 0f, mc.player.onGround))
        }
        super.onUpdate()
    }
}