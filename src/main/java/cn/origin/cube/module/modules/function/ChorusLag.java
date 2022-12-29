package cn.origin.cube.module.modules.function;

import cn.origin.cube.event.events.client.PacketEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.settings.IntegerSetting;
import java.util.LinkedList;
import java.util.Queue;

import cn.origin.cube.utils.client.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ChorusLag",
        descriptions = "ChorusLag",
        category = Category.FUNCTION)
public class ChorusLag  extends Module {

    IntegerSetting sDelay = registerSetting("TickDelay", 18, 0, 500);

    int delay = 0;
    int delay2 = 0;
    boolean ateChorus = false;
    boolean hackPacket = false;
    boolean posTp = false;
    double posX;
    double posY;
    double posZ;
    Queue<CPacketPlayer> packets = new LinkedList<CPacketPlayer>();
    Queue<CPacketConfirmTeleport> packetss = new LinkedList<CPacketConfirmTeleport>();

    @SubscribeEvent
    public void onSend(PacketEvent.Send event){
        if (event.getPacket() instanceof CPacketConfirmTeleport && this.ateChorus && this.delay2 < this.sDelay.getValue()) {
            this.packetss.add((CPacketConfirmTeleport)event.getPacket());
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer && this.ateChorus && this.delay2 < this.sDelay.getValue()) {
            this.packets.add((CPacketPlayer)event.getPacket());
            event.setCanceled(true);
        }
    }

    @Override
    public void onEnable() {
        this.ateChorus = false;
        this.hackPacket = false;
        this.posTp = false;
    }

    @Override
    public void onUpdate() {
        if (this.ateChorus) {
            ++this.delay;
            ++this.delay2;
            if (!ChorusLag.mc.player.getPosition().equals((Object)new BlockPos(this.posX, this.posY, this.posZ)) && !this.posTp && ChorusLag.mc.player.getDistance(this.posX, this.posY, this.posZ) > 1.0) {
                ChorusLag.mc.player.setPosition(this.posX, this.posY, this.posZ);
                this.posTp = true;
            }
        }
        if (this.ateChorus && this.delay2 > this.sDelay.getValue()) {
            this.ateChorus = false;
            this.delay = 0;
            this.hackPacket = true;
            this.delay2 = 0;
            this.sendPackets();
        }
        if (this.delay2 == this.sDelay.getValue() - 40) {
            ChatUtil.sendColoredMessage("Chorusing in 2 seconds");
        }
    }

    public void sendPackets() {
        while (!this.packets.isEmpty()) {
            ChorusLag.mc.player.connection.sendPacket((Packet)this.packets.poll());
        }
        while (!this.packetss.isEmpty()) {
            ChorusLag.mc.player.connection.sendPacket((Packet)this.packetss.poll());
        }
        this.hackPacket = false;
        this.delay2 = 0;
        this.ateChorus = false;
    }

    @SubscribeEvent
    public void finishEating(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() == ChorusLag.mc.player && event.getResultStack().getItem().equals((Object)Items.CHORUS_FRUIT)) {
            this.posX = ChorusLag.mc.player.posX;
            this.posY = ChorusLag.mc.player.posY;
            this.posZ = ChorusLag.mc.player.posZ;
            this.posTp = false;
            this.ateChorus = true;
        }
    }
}

