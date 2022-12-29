package cn.origin.cube.module.modules.combat.AutoCrystal;

import cn.origin.cube.Cube;
import cn.origin.cube.event.events.client.PacketEvent;
import cn.origin.cube.event.events.world.Render3DEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.module.modules.combat.KillAura;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.settings.ModeSetting;
import cn.origin.cube.utils.Timer;
import cn.origin.cube.utils.client.MathUtil;
import cn.origin.cube.utils.player.EntityUtil;
import cn.origin.cube.utils.player.InventoryUtil;
import cn.origin.cube.utils.render.Render2DUtil;
import cn.origin.cube.utils.render.Render3DUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ModuleInfo(name = "AutoCrystal", descriptions = "Auto attack entity", category = Category.COMBAT)
public class AutoCrystal extends Module {

    public BooleanSetting switchToCrystal = registerSetting("Switch", false);
    public BooleanSetting silent = registerSetting("Silent", false).booleanVisible(switchToCrystal);
    public BooleanSetting multiThread = registerSetting("MultiThread", false);
    public BooleanSetting ak47 = registerSetting("Machine Gun", false);
    public BooleanSetting players = registerSetting("Players", false);
    public BooleanSetting mobs = registerSetting("Hostiles", false);
    public BooleanSetting passives = registerSetting("Passives", false);
    public BooleanSetting place = registerSetting("Place", false);
    public BooleanSetting explode = registerSetting("Break", false);
    public BooleanSetting instant = registerSetting("Instant", false).booleanVisible(explode);
    public IntegerSetting range = registerSetting("Range", 5, 0, 6);
    public IntegerSetting minDamage = registerSetting("MinimumDmg", 4, 0, 20);
    public IntegerSetting selfDamage = registerSetting("SelfDamage", 10, 0, 20);
    public BooleanSetting antiWeakness = registerSetting("AntiWeakness", false);
    public BooleanSetting silentAntiWeakness = registerSetting("Silent", false).booleanVisible(antiWeakness);
    public BooleanSetting multiPlace = registerSetting("Multi-Place", false);
    public BooleanSetting rotate = registerSetting("Rotate", false);
    public BooleanSetting autoTimerl = registerSetting("Manual-Timer", false);
    public BooleanSetting rayTrace = registerSetting("Ray-trace", false);
    public BooleanSetting predict = registerSetting("Predict", false);
    public BooleanSetting packetPlace = registerSetting("PacketPlace", false);
    public BooleanSetting packetExplode = registerSetting("PacketExplode", false);
    public ModeSetting<Mode> breakHand = registerSetting("Swing", Mode.Main);
    public IntegerSetting breakSpeed = registerSetting("BreakSpeed", 20, 0, 20);
    public IntegerSetting placeSpeed = registerSetting("PlaceSpeed", 20, 0, 20);
    public BooleanSetting thinking = registerSetting("Thinking", false);
    public BooleanSetting cancelCrystal = registerSetting("Cancel Crystal", true);
    public BooleanSetting inhibit = registerSetting("Inhibit", false);
    public BooleanSetting outline = registerSetting("Outline", true);
    public IntegerSetting alpha = registerSetting("Alpha", 150, 0, 255);
    public BooleanSetting targetHud = registerSetting("Target Hud", false);
    public IntegerSetting tx = registerSetting("Alpha", 150, 0, 1000);
    public IntegerSetting ty = registerSetting("Alpha", 150, 0, 1000);

    public BlockPos render;
    public Entity renderEnt;
    private static boolean togglePitch = false;
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int newSlot;
    private int breaks;
    private String arrayListEntityName;
    private final List<Integer> deadCrystals = new ArrayList<>();
    private final Map<Integer, Long> attackedCrystals = new ConcurrentHashMap<>();
    private final List<Integer> explosionPackets = new ArrayList<>();
    private final List<BlockPos> placementPackets = new ArrayList<>();

    private static boolean autoTimeractive;
    private static boolean cancelingCrystals;
    private static boolean Isthinking;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    private int x = tx.getValue();
    private int y = ty.getValue();
    private int width = 200;
    private int height = 100;

    private Timer breakTimer = new Timer();
    private Timer placeTimer = new Timer();
    private Timer timer = new Timer();

    public static boolean isCancelingCrystals() {
        return cancelingCrystals;
    }

    @Override
    public void onUpdate() {
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);
        if (explode.getValue() && crystal != null && mc.player.getDistance(crystal) <= range.getValue() && mc.player.getHealth() >= selfDamage.getValue()) {
            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if(!silentAntiWeakness.getValue()) {
                    if (!isAttacking) {
                        oldSlot = mc.player.inventory.currentItem;
                        isAttacking = true;
                    }

                    newSlot = -1;
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = mc.player.inventory.getStackInSlot(i);
                        if (stack == ItemStack.EMPTY) {
                            continue;
                        }
                        if ((stack.getItem() instanceof ItemSword)) {
                            newSlot = i;
                            break;
                        }
                        if ((stack.getItem() instanceof ItemTool)) {
                            newSlot = i;
                            break;
                        }
                    }

                    if (newSlot != -1) {
                        mc.player.inventory.currentItem = newSlot;
                        switchCooldown = true;
                    }
                }else{
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(newSlot));
                }
            }
            if (ak47.getValue()) {
                crystal.setDead();
            }
            lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
            if (predict.getValue()) {
                final CPacketUseEntity attackPacket = new CPacketUseEntity();
                mc.player.connection.sendPacket((Packet)attackPacket);
            }
            if (isDesynced()) {
                if (breakTimer.passedTicks(5)) {
                    mc.player.setSneaking(true);
                    mc.player.setSneaking(false);
                    breakTimer.reset();
                }
            }
            if (timer.getPassedTimeMs() / 50 >= 20 - breakSpeed.getValue()) {
                timer.reset();
                if(packetExplode.getValue()){
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    CPacketUseEntity packet = new CPacketUseEntity();
                    packet.entityId = crystal.entityId;
                    packet.action = CPacketUseEntity.Action.ATTACK;
                    mc.player.connection.sendPacket(packet);
                }else{
                    mc.playerController.attackEntity(mc.player, crystal);
                }
                mc.player.swingArm(getHandToBreak());
            }
            breaks++;
            if (breaks == 2 && multiPlace.getValue()) {
                if (rotate.getValue()) {
                    resetRotation();
                }
                breaks = 0;
                return;
            } else if (!multiPlace.getValue() && breaks == 1) {
                if (!multiPlace.getValue()) {
                    resetRotation();
                }
                breaks = 0;
                return;
            }
        } else {
            if (rotate.getValue()) {
                resetRotation();
            }
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }

        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = findCrystalBlocks();
        List<Entity> entities = new ArrayList<>();
        if (players.getValue()) {
            if(multiThread.getValue()) {
                Cube.threadManager.run(() -> entities.addAll(mc.world.playerEntities));
            }else {
                entities.addAll(mc.world.playerEntities);
            }
        }
        entities.addAll(mc.world.loadedEntityList.stream().filter(entity -> EntityUtil.isLiving(entity) && (EntityUtil.isPassive(entity) ? passives.getValue() : mobs.getValue())).collect(Collectors.toList()));

        BlockPos q = null;
        double damage = .5;
        for (Entity entity : entities) {
            if (entity == mc.player || ((EntityLivingBase) entity).getHealth() <= 0) {
                continue;
            }
            for (BlockPos blockPos : blocks) {
                double b = entity.getDistanceSq(blockPos);
                if (b >= 169) {
                    continue;
                }
                double d = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, entity);
                if (d < minDamage.getValue()) {
                    continue;
                }
                if (d > damage) {
                    double self = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, mc.player);
                    if ((self > d && !(d < ((EntityLivingBase) entity).getHealth())) || self - .5 > mc.player.getHealth()) {
                        continue;
                    }
                    damage = d;
                    q = blockPos;
                    renderEnt = entity;
                    arrayListEntityName = renderEnt.getName();
                }
            }
        }
        if (damage == .5) {
            render = null;
            renderEnt = null;
            if (rotate.getValue()) {
                resetRotation();
            }
            return;
        }
        render = q;
        //ToDo Work on Silent switch
        final int oldSlot = KillAura.mc.player.inventory.currentItem;
        if (place.getValue()) {
            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if(switchToCrystal.getValue()){
                    if(silent.getValue()) {
                        if(InventoryUtil.findItemInHotbar(Items.END_CRYSTAL) != -1) {
                            InventoryUtil.switchToHotbarSlot(crystalSlot, true);
                        }
                    }else{
                        InventoryUtil.switchToHotbarSlot(crystalSlot, false);
                    }
                    if (rotate.getValue()) {
                        resetRotation();
                    }
                    switchCooldown = true;
                }
                if(silent.getValue()){
                    InventoryUtil.switchToHotbarSlot(oldSlot, false);
                }
                return;
            }
            EnumFacing f;
            lookAtPacket(q.getX() + .5, q.getY() - .5, q.getZ() + .5, mc.player);
            if (rayTrace.getValue()) {
                RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(q.getX() + .5, q.getY() - .5d, q.getZ() + .5));
                if (result == null || result.sideHit == null) {
                    f = EnumFacing.UP;
                } else {
                    f = result.sideHit;
                }
                if (switchCooldown) {
                    switchCooldown = false;
                    return;
                }
            } else {
                f = EnumFacing.UP;
            }
            if (timer.getPassedTimeMs() / 50 >= 20 - placeSpeed.getValue()) {
                timer.reset();
                if (packetPlace.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                } else {
                    placeCrystalOnBlock(q, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }
            }
        }

        if (isSpoofingAngles) {
            if (togglePitch) {
                mc.player.rotationPitch += 0.0004;
                togglePitch = false;
            } else {
                mc.player.rotationPitch -= 0.0004;
                togglePitch = true;
            }
        }
    }

    @SubscribeEvent
    public void onPacketRecieve(PacketEvent.Receive event){
        if(event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
            if (this.inhibit.getValue()) {
                try {
                    this.renderEnt = mc.world.getEntityByID(packet.getEntityID());
                } catch (Exception ex) {
                }
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (final Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                            entity.setDead();
                            mc.world.removeEntity(entity);
                        }
                    }
                }
            }
        }
    }

    public boolean ableToPlace(BlockPos position) {
        Block placeBlock = mc.world.getBlockState(position).getBlock();

        if (!placeBlock.equals(Blocks.BEDROCK) && !placeBlock.equals(Blocks.OBSIDIAN)) {
            return false;
        }

        BlockPos nativePosition = position.up();
        BlockPos updatedPosition = nativePosition.up();

        Block nativeBlock = mc.world.getBlockState(nativePosition).getBlock();
        if (!nativeBlock.equals(Blocks.AIR) && !nativeBlock.equals(Blocks.FIRE)) {
            return false;
        }

            Block updatedBlock = mc.world.getBlockState(updatedPosition).getBlock();
            if (!updatedBlock.equals(Blocks.AIR) && !updatedBlock.equals(Blocks.FIRE)) {
                return false;
            }

        int unsafeEntities = 0;

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(
                nativePosition.getX(), position.getY(), nativePosition.getZ(), nativePosition.getX() + 1, nativePosition.getY() + 2.0, nativePosition.getZ() + 1
        ))) {

            if (entity == null || entity.isDead || deadCrystals.contains(entity.getEntityId())) {
                continue;
            }

            if (entity instanceof EntityXPOrb) {
                continue;
            }
            if (entity instanceof EntityEnderCrystal) {

                if (attackedCrystals.containsKey(entity.getEntityId()) && entity.ticksExisted < 20) {
                    continue;
                }

                double localDamage = getDamageFromExplosion(mc.player, entity.getPositionVector(), false);

                double idealDamage = 0;

                for (Entity target : new ArrayList<>(mc.world.loadedEntityList)) {

                    if (target == null || target.equals(mc.player) || target.getEntityId() < 0 || EntityUtil.isDead(target) || Cube.friendManager.isFriend(entity.getName())) {
                        continue;
                    }

                    if (target instanceof EntityEnderCrystal) {
                        continue;
                    }

                    if (target.isBeingRidden() && target.getPassengers().contains(mc.player)) {
                        continue;
                    }

                    if (target instanceof EntityPlayer && !players.getValue() || isPassiveMob(target) && !passives.getValue() || isNeutralMob(target) && !passives.getValue() || isHostileMob(target) && !mobs.getValue()) {
                        continue;
                    }

                    double entityRange = mc.player.getDistance(target);

                    if (entityRange > range.getValue()) {
                        continue;
                    }

                    double targetDamage = getDamageFromExplosion(target, entity.getPositionVector(), false);
                    double safetyIndex = 1;

                    if (canTakeDamage()) {

                        double health = mc.player.getHealth();

                        if (localDamage + 0.5 > health) {
                            safetyIndex = -9999;
                        }
                            double efficiency = targetDamage - localDamage;

                            if (efficiency < 0 && Math.abs(efficiency) < 0.25) {
                                efficiency = 0;
                            }

                            safetyIndex = efficiency;

                    }

                    if (safetyIndex < 0) {
                        continue;
                    }

                    if (targetDamage > idealDamage) {
                        idealDamage = targetDamage;
                    }
                }

                if (idealDamage > 2.0) {
                    continue;
                }
            }

            unsafeEntities++;
        }
        return unsafeEntities <= 0;
    }

    public static boolean isPassiveMob(Entity entity) {

        if (entity instanceof EntityWolf) {
            return !((EntityWolf) entity).isAngry();
        }

        if (entity instanceof EntityIronGolem) {
            return ((EntityIronGolem) entity).getRevengeTarget() == null;
        }

        return entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid;
    }

    public static boolean isVehicleMob(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity)) || entity instanceof EntitySpider;
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie && !((EntityPigZombie) entity).isAngry() || entity instanceof EntityWolf && !((EntityWolf) entity).isAngry() || entity instanceof EntityEnderman && ((EntityEnderman) entity).isScreaming();
    }

    public static boolean canTakeDamage() {
        return !mc.player.capabilities.isCreativeMode;
    }

    public void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
        if(multiThread.getValue()){
            Threads threads = new Threads(ThreadType.BLOCK);
            threads.start();
            pos = pos;
        }
        RayTraceResult result = Minecraft.getMinecraft().world
                .rayTraceBlocks(
                        new Vec3d(
                                Minecraft.getMinecraft().player.posX,
                                Minecraft.getMinecraft().player.posY
                                        + (double) Minecraft.getMinecraft().player.getEyeHeight(),
                                Minecraft.getMinecraft().player.posZ),
                        new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        lookAtPacket(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, mc.player);
        Minecraft.getMinecraft().player.connection
                .sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    @Override
    public void onRender3D(Render3DEvent event){
        if(render != null || renderEnt != null){
            Render3DUtil.drawBlockBox(render, new Color(ClickGui.getCurrentColor().getRed(),ClickGui.getCurrentColor().getGreen(),ClickGui.getCurrentColor().getBlue()), outline.getValue(), 3);
        }
    }

    //ToDo add more to this

    @Override
    public void onRender2D() {
        if(targetHud.getValue()){
            Render2DUtil.drawBorderedRect(tx.getValue(), ty.getValue(),tx.getValue() + width,ty.getValue() + height, 1, new Color(35,35,35,150).getRGB(), ClickGui.getCurrentColor().getRGB());
            Cube.fontManager.CustomFont.drawString((renderEnt == null) ? "None" :renderEnt.getName(), tx.getValue() + 5, ty.getValue() + 10, ClickGui.getCurrentColor().getRGB(), true);
            Cube.fontManager.CustomFont.drawString((renderEnt == null) ? "None" : "" + renderEnt.getDistance(mc.player), tx.getValue() + 65, ty.getValue() + 10, ClickGui.getCurrentColor().getRGB(), true);
            Render2DUtil.drawGradientHRect(tx.getValue() + 20, ty.getValue() + 45,tx.getValue() + 140,ty.getValue() + 55, new Color(255, 0,0).getRGB(), new Color(0,255,0).getRGB());
        }
        super.onRender2D();
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), (float) range.getValue(), (int) range.getValue(), false, true, 0).stream().filter(this::ableToPlace).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public boolean isDesynced() {
        if (mc.isSingleplayer()) {
            return false;
        }
        return explosionPackets.size() > 40 || placementPackets.size() > 40;
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

    public float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    private void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
        cancelingCrystals = true;
        Isthinking = true;
    }

    private void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    private EnumHand getHandToBreak() {
        if (breakHand.getValue().equals(Mode.Offhand)) {
            return EnumHand.OFF_HAND;
        }
        return EnumHand.MAIN_HAND;
    }

    private void Thinking() {
        if(Isthinking) {
            this.rayTrace.setValue(true);
            this.range.setValue(6);
            this.breakSpeed.setValue(20);
            this.timer.reset();
        }
    }

    public void CancelingCrystals() {
        if(cancelCrystal.getValue()) {
            this.thinking.setValue(true);
            mc.world.removeAllEntities();
            mc.world.getLoadedEntityList();
            this.timer.reset();

        }
            if(autoTimerl.getValue()) {

                this.timer.reset();
            }
    }

    public static float getDamageFromExplosion(Entity entity, Vec3d vector, boolean blockDestruction) {
        return calculateExplosionDamage(entity, vector, 6, blockDestruction);
    }

    public static float calculateExplosionDamage(Entity entity, Vec3d vector, float explosionSize, boolean blockDestruction) {

        double doubledExplosionSize = explosionSize * 2.0;
        double dist = entity.getDistance(vector.x, vector.y, vector.z) / doubledExplosionSize;
        if (dist > 1) {
            return 0;
        }

        double v = (1 - dist) * getBlockDensity(blockDestruction, vector, entity.getEntityBoundingBox());
        float damage = CombatRules.getDamageAfterAbsorb(getScaledDamage((float) ((v * v + v) / 2.0 * 7.0 * doubledExplosionSize + 1.0)), ((EntityLivingBase) entity).getTotalArmorValue(), (float) ((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        DamageSource damageSource = DamageSource.causeExplosionDamage(new Explosion(entity.world, entity, vector.x, vector.y, vector.z, (float) doubledExplosionSize, false, true));

        int n = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), damageSource);
        if (n > 0) {
            damage = CombatRules.getDamageAfterMagicAbsorb(damage, n);
        }

        if (((EntityLivingBase) entity).isPotionActive(MobEffects.RESISTANCE)) {
            PotionEffect potionEffect = ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.RESISTANCE);
            if (potionEffect != null) {
                damage = damage * (25.0F - (potionEffect.getAmplifier() + 1) * 5) / 25.0F;
            }
        }

        return Math.max(damage, 0);
    }


    public static double getBlockDensity(boolean blockDestruction, Vec3d vector, AxisAlignedBB bb) {

        double diffX = 1 / ((bb.maxX - bb.minX) * 2D + 1D);
        double diffY = 1 / ((bb.maxY - bb.minY) * 2D + 1D);
        double diffZ = 1 / ((bb.maxZ - bb.minZ) * 2D + 1D);
        double diffHorizontal = (1 - Math.floor(1D / diffX) * diffX) / 2D;
        double diffTranslational = (1 - Math.floor(1D / diffZ) * diffZ) / 2D;

        if (diffX >= 0 && diffY >= 0 && diffZ >= 0) {

            float solid = 0;
            float nonSolid = 0;

            for (double x = 0; x <= 1; x = x + diffX) {
                for (double y = 0; y <= 1; y = y + diffY) {
                    for (double z = 0; z <= 1; z = z + diffZ) {

                        double scaledDiffX = bb.minX + (bb.maxX - bb.minX) * x;
                        double scaledDiffY = bb.minY + (bb.maxY - bb.minY) * y;
                        double scaledDiffZ = bb.minZ + (bb.maxZ - bb.minZ) * z;

                        if (!isSolid(new Vec3d(scaledDiffX + diffHorizontal, scaledDiffY, scaledDiffZ + diffTranslational), vector, blockDestruction)) {
                            solid++;
                        }

                        nonSolid++;
                    }
                }
            }

            return solid / nonSolid;
        } else {
            return 0;
        }
    }

    public static boolean isSolid(Vec3d start, Vec3d end, boolean blockDestruction) {

        if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
            if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {

                int currX = MathHelper.floor(start.x);
                int currY = MathHelper.floor(start.y);
                int currZ = MathHelper.floor(start.z);

                int endX = MathHelper.floor(end.x);
                int endY = MathHelper.floor(end.y);
                int endZ = MathHelper.floor(end.z);

                BlockPos blockPos = new BlockPos(currX, currY, currZ);
                IBlockState blockState = mc.world.getBlockState(blockPos);
                Block block = blockState.getBlock();

                if ((blockState.getCollisionBoundingBox(mc.world, blockPos) != Block.NULL_AABB) && block.canCollideCheck(blockState, false) && !blockDestruction) {
                    RayTraceResult collisionInterCheck = blockState.collisionRayTrace(mc.world, blockPos, start, end);

                    return collisionInterCheck != null;
                }

                double seDeltaX = end.x - start.x;
                double seDeltaY = end.y - start.y;
                double seDeltaZ = end.z - start.z;

                int steps = 200;

                while (steps-- >= 0) {

                    if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
                        return false;
                    }

                    if (currX == endX && currY == endY && currZ == endZ) {
                        return false;
                    }

                    boolean unboundedX = true;
                    boolean unboundedY = true;
                    boolean unboundedZ = true;

                    double stepX = 999;
                    double stepY = 999;
                    double stepZ = 999;
                    double deltaX = 999;
                    double deltaY = 999;
                    double deltaZ = 999;

                    if (endX > currX) {
                        stepX = currX + 1;
                    } else if (endX < currX) {
                        stepX = currX;
                    } else {
                        unboundedX = false;
                    }

                    if (endY > currY) {
                        stepY = currY + 1.0;
                    } else if (endY < currY) {
                        stepY = currY;
                    } else {
                        unboundedY = false;
                    }

                    if (endZ > currZ) {
                        stepZ = currZ + 1.0;
                    } else if (endZ < currZ) {
                        stepZ = currZ;
                    } else {
                        unboundedZ = false;
                    }

                    if (unboundedX) {
                        deltaX = (stepX - start.x) / seDeltaX;
                    }

                    if (unboundedY) {
                        deltaY = (stepY - start.y) / seDeltaY;
                    }

                    if (unboundedZ) {
                        deltaZ = (stepZ - start.z) / seDeltaZ;
                    }

                    if (deltaX == 0) {
                        deltaX = -1.0E-4;
                    }

                    if (deltaY == 0) {
                        deltaY = -1.0E-4;
                    }

                    if (deltaZ == 0) {
                        deltaZ = -1.0E-4;
                    }

                    EnumFacing facing;

                    if (deltaX < deltaY && deltaX < deltaZ) {
                        facing = endX > currX ? EnumFacing.WEST : EnumFacing.EAST;
                        start = new Vec3d(stepX, start.y + seDeltaY * deltaX, start.z + seDeltaZ * deltaX);
                    } else if (deltaY < deltaZ) {
                        facing = endY > currY ? EnumFacing.DOWN : EnumFacing.UP;
                        start = new Vec3d(start.x + seDeltaX * deltaY, stepY, start.z + seDeltaZ * deltaY);
                    } else {
                        facing = endZ > currZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        start = new Vec3d(start.x + seDeltaX * deltaZ, start.y + seDeltaY * deltaZ, stepZ);
                    }

                    currX = MathHelper.floor(start.x) - (facing == EnumFacing.EAST ? 1 : 0);
                    currY = MathHelper.floor(start.y) - (facing == EnumFacing.UP ? 1 : 0);
                    currZ = MathHelper.floor(start.z) - (facing == EnumFacing.SOUTH ? 1 : 0);

                    blockPos = new BlockPos(currX, currY, currZ);
                    blockState = mc.world.getBlockState(blockPos);
                    block = blockState.getBlock();

                    if (block.canCollideCheck(blockState, false) && !blockDestruction) {
                        RayTraceResult collisionInterCheck = blockState.collisionRayTrace(mc.world, blockPos, start, end);

                        return collisionInterCheck != null;
                    }
                }
            }
        }

        return false;
    }

    public final EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        EntityEnderCrystal bestCrystal = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;
            for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
                if (mc.player.getDistanceSq(target) > MathUtil.square(range.getValue())) continue;
                if (predict.getValue() && target != mc.player && this.timer.getPassedTimeMs() > this.breakSpeed.getValue().longValue()) {
                    float f = target.width / 2.0F, f1 = target.height;
                    target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
                    Entity y = renderEnt;
                    target.setEntityBoundingBox(y.getEntityBoundingBox());
                }
                double targetDamage = this.calculateDamage(crystal, target);
                if (targetDamage == 0) continue;
                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestCrystal = crystal;
                }
            }
        }
        return bestCrystal;
    }

    public static float getScaledDamage(float damage) {
        World world = mc.world;
        if (world == null) {
            return damage;
        }

        switch (mc.world.getDifficulty()) {
            case PEACEFUL:
                return 0;
            case EASY:
                return Math.min(damage / 2.0F + 1.0F, damage);
            case NORMAL:
            default:
                return damage;
            case HARD:
                return damage * 3.0F / 2.0F;
        }
    }

    public static AutoCrystal INSTANCE;

    public AutoCrystal() {
        INSTANCE = this;
    }


    public enum Mode{
        Main,Offhand
    }
    public enum PlaceMode{
        New
    }

    public enum ThreadType{
        BLOCK,CRYSTAL
    }
}


    final class Threads extends Thread {
        AutoCrystal.ThreadType type;
        BlockPos bestBlock;
        EntityEnderCrystal bestCrystal;

        public Threads(AutoCrystal.ThreadType type) {
            this.type = type;
        }

        @Override
        public void run() {

            if (this.type == AutoCrystal.ThreadType.BLOCK) {
                bestBlock = AutoCrystal.INSTANCE.render;
                AutoCrystal.INSTANCE.render = bestBlock;
            } else if (this.type ==AutoCrystal.ThreadType.CRYSTAL) {
                bestCrystal = AutoCrystal.INSTANCE.getBestCrystal();
                AutoCrystal.INSTANCE.renderEnt = bestCrystal;
            }
        }

    }
