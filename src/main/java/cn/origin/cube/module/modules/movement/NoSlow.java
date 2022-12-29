package cn.origin.cube.module.modules.movement;

import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.utils.player.MovementUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "NoSlow", descriptions = "NoSlow", category = Category.MOVEMENT)
public class NoSlow extends Module {

    public static NoSlow INSTANCE;
    public BooleanSetting strict = registerSetting("Strict", true);
    public BooleanSetting guiMove = registerSetting("Gui Move", true);
    public BooleanSetting soulSand = registerSetting("SoulSand", true);
    public BooleanSetting TwoBee = registerSetting("2b2t", true);
    boolean sneaking;

    public NoSlow() {
        NoSlow.INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (NoSlow.mc.player == null || NoSlow.mc.world == null) {
            return;
        }
        if (NoSlow.mc.currentScreen != null && !(NoSlow.mc.currentScreen instanceof GuiChat) && this.guiMove.getValue()) {
            NoSlow.mc.player.movementInput.moveStrafe = 0.0f;
            NoSlow.mc.player.movementInput.moveForward = 0.0f;
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindForward.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindForward.getKeyCode())) {
                final MovementInput movementInput = NoSlow.mc.player.movementInput;
                ++movementInput.moveForward;
                NoSlow.mc.player.movementInput.forwardKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.forwardKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindBack.getKeyCode())) {
                final MovementInput movementInput2 = NoSlow.mc.player.movementInput;
                --movementInput2.moveForward;
                NoSlow.mc.player.movementInput.backKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.backKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindLeft.getKeyCode())) {
                final MovementInput movementInput3 = NoSlow.mc.player.movementInput;
                ++movementInput3.moveStrafe;
                NoSlow.mc.player.movementInput.leftKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.leftKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindRight.getKeyCode())) {
                final MovementInput movementInput4 = NoSlow.mc.player.movementInput;
                --movementInput4.moveStrafe;
                NoSlow.mc.player.movementInput.rightKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.rightKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindJump.getKeyCode()));
            NoSlow.mc.player.movementInput.jump = Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindJump.getKeyCode());
        }
        if (this.strict.getValue()) {
            final Item item = NoSlow.mc.player.getActiveItemStack().getItem();
            if (this.sneaking && ((!NoSlow.mc.player.isHandActive() && item instanceof ItemFood) || item instanceof ItemBow || item instanceof ItemPotion || !(item instanceof ItemFood) || !(item instanceof ItemBow) || !(item instanceof ItemPotion))) {
                NoSlow.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoSlow.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneaking = false;
            }
        }
        if (this.strict.getValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            final Item item = NoSlow.mc.player.getActiveItemStack().getItem();
            if ((MovementUtils.isMoving((EntityPlayer)NoSlow.mc.player) && item instanceof ItemFood) || item instanceof ItemBow || item instanceof ItemPotion) {
                NoSlow.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(NoSlow.mc.player.posX), Math.floor(NoSlow.mc.player.posY), Math.floor(NoSlow.mc.player.posZ)), EnumFacing.DOWN));
            }
        }
        if(TwoBee.getValue()){
            if(mc.world != null) {
                Item item = mc.player.getActiveItemStack().getItem();
                if (sneaking && ((!mc.player.isHandActive() && item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion) || (!(item instanceof ItemFood) || !(item instanceof ItemBow) || !(item instanceof ItemPotion)))) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    sneaking = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onUseItem(final LivingEntityUseItemEvent event) {
        if (this.strict.getValue() || TwoBee.getValue() && !this.sneaking) {
            NoSlow.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoSlow.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }

    @SubscribeEvent
    public void onInputUpdate(final InputUpdateEvent event) {
        if (NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            final MovementInput movementInput = event.getMovementInput();
            movementInput.moveStrafe *= 5.0f;
            final MovementInput movementInput2 = event.getMovementInput();
            movementInput2.moveForward *= 5.0f;
        }
    }
}
