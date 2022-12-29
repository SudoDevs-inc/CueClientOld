package cn.origin.cube.module.modules.combat

import cn.origin.cube.event.events.world.Render3DEvent
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.module.modules.client.ClickGui
import cn.origin.cube.settings.BooleanSetting
import cn.origin.cube.settings.FloatSetting
import cn.origin.cube.utils.client.MathUtil
import cn.origin.cube.utils.player.BlockUtil
import cn.origin.cube.utils.player.EntityUtil
import cn.origin.cube.utils.render.Render3DUtil
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

@ModuleInfo(name = "KotlinAura",
    descriptions = "Render current block",
    category = Category.COMBAT)
class KotlinAura: Module() {

    private val targetRange: FloatSetting = registerSetting("TargetRange", 10f, 0f, 15f)
    private val placeRange: FloatSetting = registerSetting("PlaceRange", 5f, 0f, 6f)
    private val breakRange: FloatSetting = registerSetting("BreakRange", 5f, 0f, 6f)
    private val minDamage: FloatSetting = registerSetting("minDamage", 6f, 0f, 12f)
    private val maxSelfDamage: FloatSetting = registerSetting("maxSelfDamage", 8f, 0f, 12f)


    private val packetBreak: BooleanSetting = registerSetting("packetBreak", true)
    private val onlySelfCrystal: BooleanSetting = registerSetting("onlySelf", true)

    override fun onUpdate() {
        target = EntityUtil.getTarget(targetRange.value)
        doPlace()
        doBreak()
    }

    private var placePos: BlockPos? = null
    private var target: EntityPlayer? = null

    private fun doPlace() {
        if (target == null) return
        var maxDamage = 0.5f
        val sphere = BlockUtil.getSphere(this.placeRange.value.toDouble(), true)
        val size = sphere.size
        var i = 0
        while (i < size) {
            val pos = sphere[i]
            val self = EntityUtil.calculatePos(pos, mc.player)
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                val damage: Float = EntityUtil.calculatePos(pos, target)
                if (EntityUtil.getHealth(mc.player) > self + 0.5f && this.maxSelfDamage.value > self && EntityUtil.calculatePos(
                        pos,
                        target
                    ) > maxDamage && damage > self && !EntityUtil.isPlayerSafe(target)
                ) {
                    if (damage <= this.minDamage.value) {
                        ++i
                        continue
                    }
                    maxDamage = damage
                    placePos = pos
                }
            }
            ++i
        }
        if (placePos != null) {
            placePos?.let {
                CPacketPlayerTryUseItemOnBlock(
                    it,
                    EnumFacing.UP,
                    if (mc.player.heldItemOffhand.item === Items.END_CRYSTAL) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                    0.5f,
                    0.5f,
                    0.5f
                )
            }?.let {
                mc.connection!!.sendPacket(
                    it
                )
            }
        }
    }

    private fun doBreak() {
        for (crystal in mc.world.loadedEntityList) {
            if (crystal is EntityEnderCrystal) {
                if (onlySelfCrystal.value && placePos != null && crystal.posY.toInt() != placePos!!.y + 1) continue
                if (crystal.getDistance(mc.player) > MathUtil.square(breakRange.value.toDouble())) continue
                if (packetBreak.value) {
                    mc.connection!!.sendPacket(CPacketUseEntity(crystal))
                } else {
                    mc.playerController.attackEntity(mc.player, crystal)
                }
            }
        }
    }

    override fun onRender3D(event: Render3DEvent?) {
        if(placePos != null){
            Render3DUtil.drawBlockBox(placePos, ClickGui.getCurrentColor(), true, 2F)
        }
        super.onRender3D(event)
    }
}