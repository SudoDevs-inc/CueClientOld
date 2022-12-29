package cn.origin.cube.module.modules.function

import cn.origin.cube.event.events.client.PacketEvent
import cn.origin.cube.event.events.player.UpdateWalkingPlayerEvent
import cn.origin.cube.inject.client.ICPacketPlayer
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.FloatSetting
import cn.origin.cube.settings.ModeSetting
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.ThreadLocalRandom


@ModuleInfo(name = "NoFall",
        descriptions = "",
        category = Category.FUNCTION)
class NoFall: Module() {

    private var mode: ModeSetting<modeds> = registerSetting("Mode", modeds.Ncp)
    private var speed: FloatSetting = registerSetting("Speed", 5.0f, 1.0f, 10.0f).modeVisible(mode, modeds.Glide)
    private var fallDist: FloatSetting = registerSetting("Fall Distance", 5.0f, 1.0f, 10.0f)

    @SubscribeEvent
    fun onWalk(event: UpdateWalkingPlayerEvent?) {
        if (mode.value.equals(modeds.Ncp)) {
            mc.connection?.sendPacket(CPacketPlayer(ThreadLocalRandom.current().nextBoolean()))
        }
        if (mode.value.equals(modeds.anti)) {
            mc.connection?.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1, mc.player.posZ, true))
        }
    }

    override fun onUpdate() {
        if(mode.value.equals(modeds.Glide)){
            mc.player.motionY /= speed.value
        }
        if(mode.value.equals(modeds.Predict)){
            val vec = Vec3d(
                mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.renderPartialTicks,
                mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.renderPartialTicks,
                mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.renderPartialTicks
            )
            val pos = BlockPos(vec.x, vec.y - 2, vec.z)
            val posList = arrayOf<BlockPos>(pos.north(), pos.south(), pos.east(), pos.west(), pos.down(), pos.down())
            for (blockPos in posList) {
                val block: Block = mc.world.getBlockState(blockPos).block
                if (mc.player.dimension == 1) {
                    if (mc.player.fallDistance > fallDist.getValue()) {
                        mc.player.connection.sendPacket(CPacketPlayer.Position(0.0, 64.0, 0.0, false))
                        mc.player.fallDistance = fallDist.value + 1
                    }
                    if (mc.player.fallDistance > fallDist.value && block !== Blocks.AIR) {
                        mc.player.connection.sendPacket(CPacketPlayer.Position(0.0, 64.0, 0.0, false))
                        mc.player.fallDistance = 0f
                    }
                } else {
                    if (mc.player.fallDistance > fallDist.getValue()) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                0.0,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.fallDistance = fallDist.value + 1
                    }
                    if (mc.player.fallDistance > fallDist.value && block !== Blocks.AIR) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                0.0,
                                mc.player.posZ,
                                false
                            )
                        )
                        mc.player.fallDistance = 0f
                    }
                }
            }
        }
        if(mode.value.equals(modeds.OldFag)){
            if (mc.player.fallDistance > fallDist.value) {
                mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX, 0.0, mc.player.posZ, false))
                mc.player.fallDistance = 0f
            }
        }
        super.onUpdate()
    }

    @SubscribeEvent
    fun onSend(event: PacketEvent.Send) {
        if (event.getPacket<Packet<*>>() is CPacketPlayer) return
        if(mode.value == modeds.Offground){
            (event.getPacket() as ICPacketPlayer).setOnGround(false)
        }
        if(mode.value == modeds.OnGround){
            (event.getPacket() as ICPacketPlayer).setOnGround(true)
        }
    }

    override fun getHudInfo(): String {
        return mode.value.name
    }
    private fun shouldCancel() =
        !mc.player.isElytraFlying && !mc.player.capabilities.allowFlying && mc.player.fallDistance > 3

    enum class modeds {
        Ncp, Predict, anti, OnGround, Offground, Glide, OldFag
    }
}