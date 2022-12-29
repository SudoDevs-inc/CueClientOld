package cn.origin.cube.module.modules.combat

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.IntegerSetting
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.math.BlockPos


@ModuleInfo(name = "AutoBowRelease",
    descriptions = "AutoBowRelease",
    category = Category.COMBAT)
class AutoBowRelease:Module() {

    private val ticks: IntegerSetting = registerSetting("Ticks", 3, 0, 20)


    override fun onUpdate() {
        if (mc.player.getHeldItemMainhand()
                .getItem() === Items.BOW && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() > ticks.value
        ) {
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    mc.player.getHorizontalFacing()
                )
            )
            mc.player.stopActiveHand()
        }
    }
}