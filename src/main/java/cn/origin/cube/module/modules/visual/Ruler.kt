package cn.origin.cube.module.modules.visual

import cn.origin.cube.event.events.world.Render3DEvent
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.module.modules.client.ClickGui
import cn.origin.cube.utils.client.ChatUtil
import cn.origin.cube.utils.render.Render3DUtil
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Mouse

@ModuleInfo(name = "Ruler",
    descriptions = "No",
    category = Category.VISUAL)
class Ruler: Module() {

    var rulerBlock: BlockPos? = null

    @SubscribeEvent
    fun onClick(event: InputEvent.MouseInputEvent?) {
        if (Mouse.getEventButtonState()) {
            if (Mouse.isButtonDown(0)) {
                if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) return
                if (mc.world.getBlockState(mc.objectMouseOver.blockPos).block === Blocks.AIR) return
                if (rulerBlock == null) {
                    rulerBlock = mc.objectMouseOver.blockPos
                    ChatUtil.sendColoredMessage("Block Set")
                } else {
                    val blocksBetween = rulerBlock!!.getDistance(
                        mc.objectMouseOver.blockPos.x,
                        mc.objectMouseOver.blockPos.y,
                        mc.objectMouseOver.blockPos.z
                    ).toInt()
                    ChatUtil.sendColoredMessage("distance: $blocksBetween")
                    rulerBlock = null
                }
            }
        }
    }

    override fun onRender3D(event: Render3DEvent?) {
        if (fullNullCheck()) return;
        if (rulerBlock == null) return;
        Render3DUtil.drawBlockBox(rulerBlock, ClickGui.getCurrentColor(), true, 3F)
        super.onRender3D(event)
    }
}