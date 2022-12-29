package cn.origin.cube.module.modules.world

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.ModeSetting
import cn.origin.cube.utils.player.BlockUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockObsidian
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d

@ModuleInfo(name = "AntiVoid", descriptions = "Prevents you from taking fall damage", category = Category.WORLD)
class AntiVoid: Module() {

    val mode: ModeSetting<Mode> = registerSetting("Mode", Mode.Jump)

    override fun onUpdate() {
        if (mc.player == null || mc.world == null) return
        if (isAboveVoid() && !mc.player.isSneaking) {
            if (mode.value.equals("Place")) {
                val slot = getObsidianSlot()
                if (slot != -1) {
                    BlockUtil.placeBlock(BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ), EnumHand.MAIN_HAND, true, true)
                }
            } else if (mode.value.equals("NoFall")) {
                mc.player.motionY = 0.0
            } else {
                mc.player.moveVertical = 5.0f
                mc.player.jump()
            }
        } else {
            mc.player.moveVertical = 0.0f
        }
    }

    private fun isAboveVoid(): Boolean {
        if (mc.player.posY <= 3) {
            val trace = mc.world.rayTraceBlocks(
                mc.player.positionVector,
                Vec3d(mc.player.posX, 0.0, mc.player.posZ),
                false,
                false,
                false
            )
            return trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK
        }
        return false
    }

    fun getObsidianSlot(): Int {
        var slot = -1
        for (i in 0..8) {
            val stack = mc.player.inventory.getStackInSlot(i)
            if (stack == ItemStack.EMPTY || stack.item !is ItemBlock) continue
            val block: Block = (stack.item as ItemBlock).block
            if (block is BlockObsidian) {
                slot = i
                break
            }
        }
        return slot
    }

    enum class Mode{
        Place, Nofall, Jump
    }
}