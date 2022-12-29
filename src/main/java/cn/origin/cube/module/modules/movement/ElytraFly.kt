package cn.origin.cube.module.modules.movement

import cn.origin.cube.event.events.player.TravelEvent
import cn.origin.cube.inject.client.IMinecraft
import cn.origin.cube.inject.client.ITimer
import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.BooleanSetting
import cn.origin.cube.settings.FloatSetting
import cn.origin.cube.settings.ModeSetting
import cn.origin.cube.utils.Utils
import net.minecraft.block.Block
import net.minecraft.client.renderer.EnumFaceDirection
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.item.EnumAction
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "ElytraFly",
    descriptions = "ElytraFly",
    category = Category.MOVEMENT)
class ElytraFly:Module() {

    private val mode: ModeSetting<Mode> = registerSetting("Mode", Mode.CONTROL)

    private var ascendPitch: FloatSetting = registerSetting("AscendPitch", -45f, -90f, 90f)
    private var descendPitch: FloatSetting = registerSetting("DescendPitch", 45f, -90f, 90f)
    private var lockPitch: BooleanSetting = registerSetting("LockPitch", true)
    private var lockPitchVal: FloatSetting = registerSetting("LockedPitch", 0f, -90f, 90f)

    private var cancelMotion = registerSetting("CancelMotion", false)

    private var flySpeed: FloatSetting = registerSetting("FlySpeed", 1f, 0.1f, 2f)
    private var ascend = registerSetting("AscendSpeed", 1.0, 0.1, 2.0)
    private var descend = registerSetting("DescendSpeed", 1.0, 0.1, 2.0)
    private var fallSpeed: FloatSetting = registerSetting("FallSpeed", 0f, 0f, 0.1f)

    private var takeOff: BooleanSetting = registerSetting("Takeoff", false)
    private var takeOffTimer: FloatSetting = registerSetting("Timer", 0.2f, 0.1f, 1f)

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }

        if (takeOff.value) {
            if (!mc.player.isElytraFlying) {

                ((mc as IMinecraft).hookGetTimer() as ITimer).hookSetTickLength(50 / takeOffTimer.value)

                if (mc.player.onGround) {
                    mc.player.jump()
                }
                else {
                    mc.player.connection.sendPacket(
                        CPacketEntityAction(
                            mc.player, CPacketEntityAction.Action.START_FALL_FLYING
                        )
                    )
                }
            }
        }
    }

    override fun onDisable() {
        ((mc as IMinecraft).hookGetTimer() as ITimer).hookSetTickLength(50f)
    }

    @SubscribeEvent
    fun onTravel(travelEvent: TravelEvent) {
        if (fullNullCheck()) {
            return
        }
        if (mc.player.isElytraFlying) {
            ((mc as IMinecraft).hookGetTimer() as ITimer).hookSetTickLength(50f)
            if (mode.value != Mode.BOOST) {
                travelEvent.isCanceled
                stopMotion(-fallSpeed.value)
            }
            else {
                if (cancelMotion.value) {
                    travelEvent.isCanceled
                    stopMotion(-fallSpeed.value)
                }
            }

            when (mode.value) {
                Mode.CONTROL -> {
                    move(flySpeed.value)
                    handleControl()
                }

                Mode.STRICT -> {
                    move(flySpeed.value)
                    handleStrict()
                }

                Mode.BOOST -> if (mc.gameSettings.keyBindForward.isKeyDown && !(mc.player.posX - mc.player.lastTickPosX > flySpeed.value || mc.player.posZ - mc.player.lastTickPosZ > flySpeed.value)) {
                    propel(flySpeed.value * if (cancelMotion.value) 1f else 0.015f)
                }
            }
            lockLimbs()
        }
    }

    private fun handleControl() {
        if (mc.gameSettings.keyBindJump.isKeyDown) {
            mc.player.motionY = ascend.value
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.player.motionY = -descend.value
        }
    }

    private fun handleStrict() {
        if (mc.gameSettings.keyBindJump.isKeyDown) {
            mc.player.rotationPitch = ascendPitch.value
            mc.player.motionY = ascend.value
        }
        else if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.player.rotationPitch = descendPitch.value
            mc.player.motionY = -descend.value
        }
        else {
            if (lockPitch.value) {
                mc.player.rotationPitch = lockPitchVal.value
            }
        }
    }

    enum class Mode {
        CONTROL,
        STRICT,
        BOOST
    }

    fun stopMotion(fallSpeed: Float) {
        Utils.mc.player.setVelocity(0.0, fallSpeed.toDouble(), 0.0)
    }

    val isCollided: Boolean
        get() = Utils.mc.player.collidedHorizontally || Utils.mc.player.collidedVertically

    val isInLiquid: Boolean
        get() = Utils.mc.player.isInWater || Utils.mc.player.isInLava


    fun lockLimbs() {
        Utils.mc.player.prevLimbSwingAmount = 0f
        Utils.mc.player.limbSwingAmount = 0f
        Utils.mc.player.limbSwing = 0f
    }

    val isMoving: Boolean
        get() = Utils.mc.player.movementInput.moveForward != 0f || Utils.mc.player.movementInput.moveStrafe != 0f || Utils.mc.player.posX != Utils.mc.player.lastTickPosX || Utils.mc.player.posZ != Utils.mc.player.lastTickPosZ


    fun move(speed: Float) {
        val mover = if (Utils.mc.player.isRiding) Utils.mc.player.ridingEntity else Utils.mc.player
        var forward = Utils.mc.player.movementInput.moveForward
        var strafe = Utils.mc.player.movementInput.moveStrafe
        var playerYaw = Utils.mc.player.rotationYaw

        if (mover != null) {
            if (forward != 0f) {
                if (strafe >= 1) {
                    playerYaw += (if (forward > 0) -45 else 45).toFloat()
                    strafe = 0f
                }
                else if (strafe <= -1) {
                    playerYaw += (if (forward > 0) 45 else -45).toFloat()
                    strafe = 0f
                }

                if (forward > 0) {
                    forward = 1f
                }
                else if (forward < 0) {
                    forward = -1f
                }
            }

            val sin = sin(Math.toRadians((playerYaw + 90).toDouble()))
            val cos = cos(Math.toRadians((playerYaw + 90).toDouble()))

            mover.motionX = forward.toDouble() * speed * cos + strafe.toDouble() * speed * sin
            mover.motionZ = forward.toDouble() * speed * sin - strafe.toDouble() * speed * cos

            mover.stepHeight = 0.6f

            if (!isMoving) {
                mover.motionX = 0.0
                mover.motionZ = 0.0
            }
        }
    }

    fun forward(speed: Double): Vec3d {
        var forwardInput = Utils.mc.player.movementInput.moveForward
        var strafeInput = Utils.mc.player.movementInput.moveStrafe
        var playerYaw = Utils.mc.player.prevRotationYaw + (Utils.mc.player.rotationYaw - Utils.mc.player.prevRotationYaw) * Utils.mc.renderPartialTicks

        if (forwardInput != 0.0f) {
            if (strafeInput > 0.0f) {
                playerYaw += (if (forwardInput > 0.0f) -45 else 45).toFloat()
            }
            else if (strafeInput < 0.0f) {
                playerYaw += (if (forwardInput > 0.0f) 45 else -45).toFloat()
            }

            strafeInput = 0.0f

            if (forwardInput > 0.0f) {
                forwardInput = 1.0f
            }
            else if (forwardInput < 0.0f) {
                forwardInput = -1.0f
            }
        }

        val sin = sin(Math.toRadians((playerYaw + 90.0f).toDouble()))
        val cos = cos(Math.toRadians((playerYaw + 90.0f).toDouble()))

        val posX = forwardInput * speed * cos + strafeInput * speed * sin
        val posZ = forwardInput * speed * sin - strafeInput * speed * cos

        return Vec3d(posX, Utils.mc.player.posY, posZ)
    }


    fun propel(speed: Float) {
        val yaw = Utils.mc.player.rotationYaw

        val pitch = Utils.mc.player.rotationPitch
        Utils.mc.player.motionX -= sin(Math.toRadians(yaw.toDouble())) * cos(Math.toRadians(pitch.toDouble())) * speed
        Utils.mc.player.motionZ += cos(Math.toRadians(yaw.toDouble())) * cos(Math.toRadians(pitch.toDouble())) * speed
        Utils.mc.player.motionY += -sin(Math.toRadians(pitch.toDouble())) * speed
    }

    val isPlayerEating: Boolean
        get() = Utils.mc.player.isHandActive && Utils.mc.player.activeItemStack.itemUseAction == EnumAction.EAT

    val isPlayerDrinking: Boolean
        get() = Utils.mc.player.isHandActive && Utils.mc.player.activeItemStack.itemUseAction == EnumAction.DRINK

    val isPlayerConsuming: Boolean
        get() = Utils.mc.player.isHandActive && (Utils.mc.player.activeItemStack.itemUseAction == EnumAction.EAT || Utils.mc.player.activeItemStack.itemUseAction == EnumAction.DRINK)

    val direction: EnumFaceDirection
        get() = EnumFaceDirection.getFacing(EnumFacing.fromAngle(Utils.mc.player.rotationYaw.toDouble()))

    fun getAxis(direction: EnumFaceDirection?): String {
        when (direction) {
            EnumFaceDirection.NORTH -> return "-Z"
            EnumFaceDirection.SOUTH -> return "+Z"
            EnumFaceDirection.EAST -> return "+X"
            EnumFaceDirection.WEST -> return "-X"
            else -> {}
        }

        return ""
    }

    val baseMoveSpeed: Double
        get() = 0.2873 * if (Utils.mc.player.isPotionActive(MobEffects.SPEED)) 1.0 + 0.2 * (Utils.mc.player.getActivePotionEffect(
            MobEffects.SPEED)!!.amplifier + 1.0) else 1.0

    fun getBlockUnder(player: Entity): BlockPos? {
        var pos = BlockPos(player.posX, player.posY, player.posZ)
        var blockAtPos = pos.getBlockAtPos()
        while (pos.y > -2 && blockAtPos === Blocks.AIR) {
            pos = pos.down()
            blockAtPos = pos.getBlockAtPos()
        }
        return if (pos.y < 0) null else pos
    }

    fun BlockPos.getBlockAtPos(): Block = Utils.mc.world.getBlockState(this).block
}