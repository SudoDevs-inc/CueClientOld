package cn.origin.cube.module.modules.world;

import cn.origin.cube.Cube;
import cn.origin.cube.event.events.client.PacketEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.settings.ModeSetting;
import cn.origin.cube.utils.Timer;
import cn.origin.cube.utils.client.MathUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@ModuleInfo(name = "FakePlayer", descriptions = "Auto attack entity", category = Category.WORLD)
public class FakePlayer extends Module {
    public BooleanSetting moving = registerSetting("Moving", true);
    public IntegerSetting moveDelay = registerSetting("MoveDelay", 75, 25, 250);
    public BooleanSetting popping = registerSetting("Popping", true);
    public BooleanSetting particle = registerSetting("Particle", true);
    public BooleanSetting sound = registerSetting("Sound", false);
    public BooleanSetting distanceCheck = registerSetting("DistanceCheck", true);
    public IntegerSetting distance = registerSetting("Distance", 15, 1, 30);
    public BooleanSetting inventory = registerSetting("Inventory", true);
    public ModeSetting<Mode> inventoryMode = registerSetting("InventoryMode", Mode.NORMAL);
    public BooleanSetting gapple = registerSetting("Gapple", true);
    public IntegerSetting gappleDelay = registerSetting("GappleDelay(Secs)", 5, 1, 10);

    public enum Mode{
        OP, NORMAL
    }

    public Timer gappleTimer = new Timer();
    public Timer moveTimer = new Timer();
    public EntityOtherPlayerMP fakePlayer;
    public Random random = new Random();

    @Override
    public void onEnable() {
        if (fullNullCheck()) return;
        if (fakePlayer == null) {
            fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), "CutePerson"));
            fakePlayer.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.cameraYaw, mc.player.cameraPitch);
            if (inventory.getValue()) {
                if (inventoryMode.getValue().equals(Mode.NORMAL)) {
                    fakePlayer.inventory.copyInventory(mc.player.inventory);
                }else{
                        //diamond helmet
                        ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
                        helmet.addEnchantment(Enchantments.PROTECTION, 4);
                        fakePlayer.inventory.armorInventory.set(3, helmet);
                        //diamond chest
                        ItemStack chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
                        chest.addEnchantment(Enchantments.PROTECTION, 4);
                        fakePlayer.inventory.armorInventory.set(2, chest);
                        //diamond legs
                        ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
                        leggings.addEnchantment(Enchantments.BLAST_PROTECTION, 4);
                        fakePlayer.inventory.armorInventory.set(1, leggings);
                        //diamond boots
                        ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
                        boots.addEnchantment(Enchantments.PROTECTION, 4);
                        fakePlayer.inventory.armorInventory.set(0, boots);
                }
            }
            mc.world.spawnEntity(fakePlayer);
            gappleTimer.reset();
        }
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        if (fakePlayer != null) {
            if (mc.player.getDistance(fakePlayer) >= distance.getValue()) {
                if (distanceCheck.getValue())
                    fakePlayer.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.cameraYaw, mc.player.cameraPitch);
            }
            if (gapple.getValue()) {
                if (gappleTimer.passedMs((long) (int) gappleDelay.getValue() * 1000)) {
                    fakePlayer.setAbsorptionAmount(16.0f);
                    fakePlayer.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
                    fakePlayer.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
                    fakePlayer.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
                    fakePlayer.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
                    gappleTimer.reset();
                }
            }
            if (popping.getValue()) {
                fakePlayer.inventory.offHandInventory.set(0, new ItemStack(Items.TOTEM_OF_UNDYING));
                if (fakePlayer.getHealth() <= 0) {
                    fakePop(fakePlayer);
                    fakePlayer.setHealth(20f);
                    Cube.eventManager.getTotemPopListener().handlePop(fakePlayer);
                }
            }
            if (!moving.getValue()) return;
            if (moveTimer.passedMs((long) moveDelay.getValue())) {
                if (mc.world.getBlockState(new BlockPos(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ)).getBlock() != Blocks.AIR)
                    fakePlayer.posY += 0.5f;
                else if (mc.world.getBlockState(new BlockPos(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ).down()).getBlock() == Blocks.AIR)
                    fakePlayer.posY -= 1;
                else {
                    int i = random.nextInt(2);
                    if (i == 0) {
                        fakePlayer.setPositionAndRotation(fakePlayer.posX + 0.25f, fakePlayer.posY, fakePlayer.posZ, fakePlayer.cameraYaw, fakePlayer.cameraPitch);
                    } else {
                        fakePlayer.setPositionAndRotation(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ + 0.25f, fakePlayer.cameraYaw, fakePlayer.cameraPitch);
                    }
                }
                moveTimer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fakePlayer == null)
            return;
        if (event.getPacket() instanceof SPacketExplosion) {
            final SPacketExplosion explosion = (SPacketExplosion) event.getPacket();
            if (fakePlayer.getDistance(explosion.getX(), explosion.getY(), explosion.getZ()) <= 15) {
                final double damage = calculateDamage(explosion.getX(), explosion.getY(), explosion.getZ(), fakePlayer);
                if (damage > 0 && popping.getValue())
                    fakePlayer.setHealth((float) (fakePlayer.getHealth() - MathHelper.clamp(damage, 0, 999)));
            }
        }
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) return;
        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer);
            fakePlayer = null;
        }
    }

    private void fakePop(Entity entity) {
        if (particle.getValue()) mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.TOTEM, 30);
        if (sound.getValue())
            mc.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 6.0F * 2.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 9.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage = damage * (1.0F - f / 25.0F);

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }

            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }


}
