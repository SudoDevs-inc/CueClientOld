package cn.origin.cube.module.modules.movement

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.math.MathHelper

@ModuleInfo(name = "Speed",
    descriptions = "Speed",
    category = Category.MOVEMENT)
class Speed: Module() {
    var delay = 0

    override fun onUpdate() {
        if (mc.player == null) return;
        delay++;
        if (delay >= 2) {
            this.a(0.405, 0.22f, 1.0064)
            delay = 0;
        }
        super.onUpdate()
    }

    private fun sped(): Float {
        var v = Minecraft.getMinecraft().player.rotationYaw
        if (Minecraft.getMinecraft().player.moveForward < 0.0f) {
            v += 180.0f
        }
        var v2 = 1.0f
        if (Minecraft.getMinecraft().player.moveForward < 0.0f) {
            v2 = -0.5f
        } else if (Minecraft.getMinecraft().player.moveForward > 0.0f) {
            v2 = 0.5f
        }
        if (Minecraft.getMinecraft().player.moveStrafing > 0.0f) {
            v -= 90.0f * v2
        }
        if (Minecraft.getMinecraft().player.moveStrafing < 0.0f) {
            v += 90.0f * v2
        }
        v *= 0.017453292f
        return v
    }


    private fun a(motionY: Double, n: Float, n2: Double) {
        val v2 =
            Minecraft.getMinecraft().player.moveForward != 0.0f || Minecraft.getMinecraft().player.moveForward > 0.0f
        if (v2 || Minecraft.getMinecraft().player.moveStrafing != 0.0f) {
            Minecraft.getMinecraft().player.isSprinting = true
            if (Minecraft.getMinecraft().player.onGround) {
                Minecraft.getMinecraft().player.motionY = motionY
                val a: Float = sped()
                val player = Minecraft.getMinecraft().player
                player.motionX -= (MathHelper.sin(a) * n).toDouble()
                val player2 = Minecraft.getMinecraft().player
                player2.motionZ += (MathHelper.cos(a) * n).toDouble()
            } else {
                val sqrt =
                    Math.sqrt(Minecraft.getMinecraft().player.motionX * Minecraft.getMinecraft().player.motionX + Minecraft.getMinecraft().player.motionZ * Minecraft.getMinecraft().player.motionZ)
                val n3: Double = sped().toDouble()
                Minecraft.getMinecraft().player.motionX = -Math.sin(n3) * n2 * sqrt
                Minecraft.getMinecraft().player.motionZ = Math.cos(n3) * n2 * sqrt
            }
        }
    }

    fun a(n: Double, n2: Double, entityPlayerSP: EntityPlayerSP) {
        val movementInput = Minecraft.getMinecraft().player.movementInput
        var moveForward = movementInput.moveForward
        var moveStrafe = movementInput.moveStrafe
        var rotationYaw = Minecraft.getMinecraft().player.rotationYaw
        if (moveForward.toDouble() != 0.0) {
            if (moveStrafe > 0.0) {
                rotationYaw += (if (moveForward > 0.0) -45 else 45).toFloat()
            } else if (moveStrafe < 0.0) {
                rotationYaw += (if (moveForward > 0.0) 45 else -45).toFloat()
            }
            moveStrafe = 0.0f
            if (moveForward > 0.0) {
                moveForward = 1.0f
            } else if (moveForward < 0.0) {
                moveForward = -1.0f
            }
        }
        if (moveStrafe > 0.0) {
            moveStrafe = 1.0f
        } else if (moveStrafe < 0.0) {
            moveStrafe = -1.0f
        }
        entityPlayerSP.motionX =
            n + (moveForward * 0.2 * Math.cos(Math.toRadians((rotationYaw + 90.0f).toDouble())) + moveStrafe * 0.2 * Math.sin(
                Math.toRadians((rotationYaw + 90.0f).toDouble())
            ))
        entityPlayerSP.motionZ =
            n2 + (moveForward * 0.2 * Math.sin(Math.toRadians((rotationYaw + 90.0f).toDouble())) - moveStrafe * 0.2 * Math.cos(
                Math.toRadians((rotationYaw + 90.0f).toDouble())
            ))
    }
}