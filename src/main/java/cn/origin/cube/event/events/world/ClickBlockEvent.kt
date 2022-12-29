package cn.origin.cube.event.events.world

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.Event

class ClickBlockEvent(val blockPos: BlockPos, val facing: EnumFacing) : Event()