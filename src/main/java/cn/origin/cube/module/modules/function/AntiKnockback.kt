package cn.origin.cube.module.modules.function

import cn.origin.cube.event.events.client.PacketEvent
import cn.origin.cube.inject.client.ISPacketExplosion
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.BooleanSetting
import cn.origin.cube.settings.FloatSetting
import net.minecraft.network.Packet
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketExplosion
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@ModuleInfo(name = "AntiKnockback",
    descriptions = "AntiKnockback",
    category = Category.FUNCTION)
class AntiKnockback : Module() {

    var horiz: FloatSetting = registerSetting("Horizontal", 0.0f, 0.0f, 100.0f)
    var vert: FloatSetting = registerSetting("Vertical", 0.0f, 0.0f, 100.0f)
    var explo: BooleanSetting = registerSetting("Explosion", true)
    var velo: BooleanSetting = registerSetting("Velocity", true)

    @SubscribeEvent
    fun onPacketRecieve(e: PacketEvent.Receive) {
        if (explo.value) {
            if (e.getPacket<Packet<*>>() is SPacketExplosion) {
                if (horiz.value == 0.0f && vert.value == 0.0f) {
                    e.isCanceled = true
                } else {
                    val packet = e.getPacket<Packet<*>>() as SPacketExplosion
                    val motionX = (packet as ISPacketExplosion).motionX / 100
                    val motionY = (packet as ISPacketExplosion).motionY / 100
                    val motionZ = (packet as ISPacketExplosion).motionZ / 100
                    (packet as ISPacketExplosion).motionX = motionX * horiz.value
                    (packet as ISPacketExplosion).motionY = motionY * vert.value
                    (packet as ISPacketExplosion).motionZ = motionZ * horiz.value
                }
            }
        }
        if (velo.value) {
            if (e.getPacket<Packet<*>>() is SPacketEntityVelocity) {
                if (horiz.value == 0.0f && vert.value == 0.0f) {
                    e.isCanceled = true
                } else {
                    //ToDo this
                }
            }
        }
    }

    override fun getHudInfo(): String? {
        return "H%,"+horiz.value+",V%,"+vert.value+""
    }
}