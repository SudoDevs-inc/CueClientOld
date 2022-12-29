package cn.origin.cube.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class MovementUtils {
    static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMoving(EntityPlayer player) {
        return player.moveStrafing != 0.0f || player.moveForward != 0.0f;
    }

    public static double getBlockHeight() {
        double max_y = -1;
        final AxisAlignedBB grow = mc.player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);
        if (!mc.world.getCollisionBoxes(mc.player, grow.offset(0, 2, 0)).isEmpty()) return 100;
        for (final AxisAlignedBB aabb : mc.world.getCollisionBoxes(mc.player, grow)) {
            if (aabb.maxY > max_y) {
                max_y = aabb.maxY;
            }
        }
        return max_y - mc.player.posY;
    }

    public static double[] getMoveSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (!isMoving(mc.player)) {
            return new double[] { 0, 0 };
        }

        else if (forward != 0) {
            if (strafe >= 1) {
                yaw += (float) (forward > 0 ? -45 : 45);
                strafe = 0;
            }

            else if (strafe <= -1) {
                yaw += (float) (forward > 0 ? 45 : -45);
                strafe = 0;
            }

            if (forward > 0)
                forward = 1;

            else if (forward < 0)
                forward = -1;
        }

        double sin = Math.sin(Math.toRadians(yaw + 90));
        double cos = Math.cos(Math.toRadians(yaw + 90));

        double motionX = (double) forward * speed * cos + (double) strafe * speed * sin;
        double motionZ = (double) forward * speed * sin - (double) strafe * speed * cos;

        return new double[] {motionX, motionZ};
    }

}
