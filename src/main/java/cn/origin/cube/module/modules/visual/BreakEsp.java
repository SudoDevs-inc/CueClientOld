package cn.origin.cube.module.modules.visual;

import cn.origin.cube.event.events.world.Render3DEvent;
import cn.origin.cube.inject.client.IRenderGlobal;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.utils.render.Render3DUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

//ToDo make more to this
@ModuleInfo(name = "BreakEsp",
        descriptions = "BreakEsp",
        category = Category.VISUAL)
public class BreakEsp extends Module {

    FloatSetting range = registerSetting("Range", 5F, 5F, 100F);

    @Override
    public void onRender3D(Render3DEvent event) {
        ((IRenderGlobal) mc.renderGlobal).getDamagedBlocks().forEach((pos, progress) -> {
            if (progress != null) {
                BlockPos blockPos = progress.getPosition();

                if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
                    return;
                }

                if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {
                    int damage = MathHelper.clamp(progress.getPartialBlockDamage(), 0, 8);

                    AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);

                    double x = bb.minX + (bb.maxX - bb.minX) / 2;
                    double y = bb.minY + (bb.maxY - bb.minY) / 2;
                    double z = bb.minZ + (bb.maxZ - bb.minZ) / 2;

                    double sizeX = damage * ((bb.maxX - x) / 8);
                    double sizeY = damage * ((bb.maxY - y) / 8);
                    double sizeZ = damage * ((bb.maxZ - z) / 8);

                    int colourFactor = (damage * 255) / 8;

                    Render3DUtil.drawBBBox(bb, ClickGui.getCurrentColor(), 160, 1F, true);
                }
            }
        });
    }
}
