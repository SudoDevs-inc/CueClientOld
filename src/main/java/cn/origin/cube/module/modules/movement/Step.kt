package cn.origin.cube.module.modules.movement

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.FloatSetting
import cn.origin.cube.settings.ModeSetting
import cn.origin.cube.utils.player.MovementUtils.getBlockHeight
import net.minecraft.network.play.client.CPacketPlayer


@ModuleInfo(name = "Step",
    descriptions = "Step up blocks easier",
    category = Category.MOVEMENT)
class Step:Module() {

    val mode: ModeSetting<Stepmode> = registerSetting("Mode", Stepmode.Normal)
    val heighty: FloatSetting = registerSetting("Height", 1f, 1f, 6f)

    override fun onUpdate() {
        if (mode.value == Stepmode.Vanilla) {
            mc.player.stepHeight = heighty.value
        } else if (mode.value == Stepmode.Normal) {
            mc.player.stepHeight = 0.6f
            if (heighty.value > 2) heighty.setValue(2f)
            val n: Double = getBlockHeight()
            if (n < 0 || n > 2) return
            if (n == 2.0 && heighty.value == 2f) {
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.42,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.78,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.63,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.51,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.9,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.21,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.45,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.43,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ)
            }
            if (n == 1.5 && heighty.value >= 1.5) {
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.41999998688698,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.7531999805212,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.00133597911214,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.16610926093821,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.24918707874468,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.1707870772188,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ)
            }
            if (n == 1.0 && heighty.value >= 1) {
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.41999998688698,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.7531999805212,
                        mc.player.posZ,
                        mc.player.onGround
                    )
                )
                mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ)
            }
        }
    }

    override fun onDisable() {
        mc.player.stepHeight = 0.6f
    }

    enum class Stepmode {
        Vanilla, Normal
    }
}