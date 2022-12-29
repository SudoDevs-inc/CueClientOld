package cn.origin.cube.module.modules.combat;

import cn.origin.cube.Cube;
import cn.origin.cube.event.events.player.UpdateWalkingPlayerEvent;
import cn.origin.cube.event.events.world.Render3DEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.DoubleSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.settings.ModeSetting;
import cn.origin.cube.utils.player.InventoryUtil;
import cn.origin.cube.utils.player.RotationUtil;
import cn.origin.cube.utils.render.Render3DUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Date;

@ModuleInfo(name = "KillAura", descriptions = "Auto attack entity", category = Category.COMBAT)
public class KillAura extends Module {
    long killLast = new Date().getTime();
    DoubleSetting hittingRange = registerSetting("Range", 5.5, 0.1, 10.0);
    IntegerSetting delay = registerSetting("Delay", 4, 0, 70);
    BooleanSetting randomD = registerSetting("RandomDelay", false);
    IntegerSetting randomDelay = registerSetting("Random Delay", 4, 0, 40).booleanVisible(randomD);
    IntegerSetting iterations = registerSetting("Iterations", 1, 1, 10);
    DoubleSetting wallsRange = registerSetting("Wall Range", 3.5, 1, 10);
    IntegerSetting ticksExisted = registerSetting("TicksExisted", 20, 0, 150);
    BooleanSetting hitDelay = registerSetting("HitDelay", true);
    BooleanSetting packet = registerSetting("PacketHit", false);
    BooleanSetting swimArm = registerSetting("SwimArm", true).booleanVisible(packet);
    BooleanSetting rotate = registerSetting("Rotate", true);
    BooleanSetting throughWalls = registerSetting("ThroughWalls", true);

    BooleanSetting playerOnly = registerSetting("PlayerOnly", true);
    BooleanSetting weaponOnly = registerSetting("WeaponOnly", true);
    BooleanSetting switchWeapon = registerSetting("SwitchWeapon", false);
    ModeSetting<currentW> weaponCurrent = registerSetting("CurrentWeapon", currentW.NONE).boolean2NVisible(switchWeapon, weaponOnly);

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        boolean isReadyAttack = mc.player.getCooledAttackStrength(0.0f) >= 1;
        if (hitDelay.getValue()) {
            if (!isReadyAttack) return;
        }
        mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> mc.player.getDistance(entity) <= hittingRange.getValue())
                .filter(entity -> !Cube.friendManager.isFriend(entity.getName()))
                .filter(entity -> entity != mc.player)
                .forEach(target -> {
                    if (playerOnly.getValue()) {
                        if (!(target instanceof EntityPlayer) ||
                                target.ticksExisted >= this.ticksExisted.getValue() &&
                                        mc.player.getDistance(target) <= this.hittingRange.getValue() &&
                                        (mc.player.canEntityBeSeen(target) || !this.throughWalls.getValue() ||
                                                mc.player.getDistance(target) <= this.wallsRange.getValue()))return;
                    }
                    if (switchWeapon.getValue()) {
                        int currentSlot;
                        if (currentW.SWORD.equals(weaponCurrent.getValue())) {
                            if ((currentSlot = InventoryUtil.findHotbarItem(ItemSword.class)) != -1) {
                                InventoryUtil.switchToHotbarSlot(currentSlot, false);
                            }
                        } else if (currentW.AXE.equals(weaponCurrent.getValue())) {
                            if ((currentSlot = InventoryUtil.findHotbarItem(ItemAxe.class)) != -1) {
                                InventoryUtil.switchToHotbarSlot(currentSlot, false);
                            }
                        }
                    }
                    if (!weaponCurrent.getValue().equals(currentW.NONE)) {
                        if (mc.player.getHeldItemMainhand() == ItemStack.EMPTY
                                || !(weaponCurrent.getValue().equals(currentW.AXE) ? ItemAxe.class : ItemSword.class)
                                .isInstance(mc.player.getHeldItemMainhand().getItem())) {
                            return;
                        }
                    }
                    final int delay = (int)(this.delay.getValue() * 10 + (randomD.getValue() ? this.randomDelay.getValue() * 10 * Math.random() : 0));
                    if (new Date().getTime() >= this.killLast + delay) {
                        this.killLast = new Date().getTime();
                        if (rotate.getValue()) rotateTo(target);
                        for (int i = 0; i < this.iterations.getValue(); ++i) {
                            attack(target);
                        }
                    }
                });

    }
    //ToDo do the rendering shit

//    @Override
//    public void onRender3D(Render3DEvent event) {
//        mc.world.loadedEntityList.stream()
//                .filter(entity -> entity instanceof EntityLivingBase)
//                .filter(entity -> mc.player.getDistance(entity) <= hittingRange.getValue())
//                .filter(entity -> !Cube.friendManager.isFriend(entity.getName()))
//                .filter(entity -> entity != mc.player)
//                .forEach(target -> {
//                    if (playerOnly.getValue()) {
//                        if (!(target instanceof EntityPlayer) ||
//                                target.ticksExisted >= this.ticksExisted.getValue() &&
//                                        mc.player.getDistance(target) <= this.hittingRange.getValue() &&
//                                        (mc.player.canEntityBeSeen(target) || !this.throughWalls.getValue() ||
//                                                mc.player.getDistance(target) <= this.wallsRange.getValue()))return;
//                    }
//                    Render3DUtil.drawBBBox(target.getCollisionBoundingBox(), ClickGui.getCurrentColor(), 100, 5, true);
//                });
//        super.onRender3D(event);
//    }

    public void attack(Entity entity) {
            rotateTo(entity);
                if (hitDelay.getValue()) {
                    if(!packet.getValue()) {
                        if (mc.player.getCooledAttackStrength(0) >= 1) {
                            mc.playerController.attackEntity(mc.player, entity);
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                    }else{
                        mc.playerController.connection.sendPacket(new CPacketUseEntity(entity));
                        if (swimArm.getValue()) mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }
                if (!hitDelay.getValue()) {
                    mc.playerController.attackEntity(mc.player, entity);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
    }

    public void rotateTo(Entity target) {
        RotationUtil.faceVector(new Vec3d(target.posX, target.posY + 1, target.posZ), true);
    }

    enum currentW {
        NONE,
        SWORD,
        AXE
    }

}