package cn.origin.cube.module.modules.visual;

import cn.origin.cube.Cube;
import cn.origin.cube.event.events.client.PacketEvent;
import cn.origin.cube.event.events.world.Render3DEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.DoubleSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.utils.Timer;
import cn.origin.cube.utils.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "SuperheroFX", descriptions = "Always light", category = Category.VISUAL)
public class SuperheroFX extends Module {

    DoubleSetting delay = registerSetting("Delay", 1.0, 0.0, 10.0);
    DoubleSetting scale = registerSetting("Scale", 1.5, 0.0, 5.0);
    IntegerSetting extra = registerSetting("Extra", 1, 0, 5);
    BooleanSetting randomColor = registerSetting("RandomColor", true);
    private List<PopupText> popTexts = new CopyOnWriteArrayList<>();
    private final Random rand = new Random();
    private final Timer timer = new Timer();
    private static final String[] superHeroTextsBlowup = new String[]{"KABOOM", "BOOM", "POW", "KAPOW", "KABLEM", "BABOOMY"};
    private static final String[] superHeroTextsDamageTaken = new String[]{"OUCH", "ZAP", "BAM", "WOW", "POW", "SLAP", "OW", "ZAM"};

    @Override
    public void onUpdate() {
        this.popTexts.removeIf(PopupText::isMarked);
        this.popTexts.forEach(PopupText::Update);
    }


    @Override
    public void onRender3D(Render3DEvent event) {
        mc.getRenderManager();
        if (mc.getRenderManager().options != null) {

            this.popTexts.forEach(pop -> {
                GlStateManager.pushMatrix();
                RenderUtil.glBillboardDistanceScaled((float) pop.pos.x, (float) pop.pos.y, (float) pop.pos.z, mc.player, scale.getValue().floatValue());
                GlStateManager.disableDepth();
                GlStateManager.translate(-((double) Cube.fontManager.badaboom.getStringWidth(pop.getDisplayName()) / 2.0), 0.0, 0.0);
                Cube.fontManager.badaboom.drawString(pop.getDisplayName(), 0, 0, pop.color);

                //added this line to not fuck up item rendering
                GlStateManager.enableDepth();

                GlStateManager.popMatrix();
            });
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;
        try {
            if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                if (mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < 20.0 && this.timer.passedMs((long) (this.delay.getValue() * 1000.0f))) {
                    this.timer.reset();
                    int len = rand.nextInt(extra.getValue());
                    for (int i = 0; i <= len; i++) {
                        Vec3d pos = new Vec3d(packet.getX() + rand.nextInt(4) - 2, packet.getY() + rand.nextInt(2), packet.getZ() + rand.nextInt(4) - 2);
                        PopupText popupText = new PopupText(ChatFormatting.ITALIC + SuperheroFX.superHeroTextsBlowup[this.rand.nextInt(SuperheroFX.superHeroTextsBlowup.length)], pos);
                        popTexts.add(popupText);
                    }
                }
            } else if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (mc.world != null) {
                    Entity e = packet.getEntity((World) mc.world);
                    if (packet.getOpCode() == 35) {
                        if (mc.player.getDistance(e) < 20.0f) {
                            PopupText popupText = new PopupText(ChatFormatting.ITALIC + "POP", e.getPositionVector().add((double) (this.rand.nextInt(2) / 2), 1.0, (double) (this.rand.nextInt(2) / 2)));
                            popTexts.add(popupText);
                        }
                    } else if (packet.getOpCode() == 2) {
                        if (mc.player.getDistance(e) < 20.0f & e != mc.player) {
                            if (this.timer.passedMs((long) (this.delay.getValue() * 1000.0f))) {
                                this.timer.reset();
                                int len = rand.nextInt((int)Math.ceil(extra.getValue()/2.0));
                                for (int i = 0; i <= len; i++) {
                                    Vec3d pos = new Vec3d(e.posX + rand.nextInt(2) - 1, e.posY + rand.nextInt(2) - 1, e.posZ + rand.nextInt(2) - 1);
                                    PopupText popupText = new PopupText(ChatFormatting.ITALIC + SuperheroFX.superHeroTextsDamageTaken[this.rand.nextInt(SuperheroFX.superHeroTextsBlowup.length)], pos);
                                    popTexts.add(popupText);
                                }
                            }
                        }
                    }
                }
            } else if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
                final int[] array = packet.getEntityIDs();
                for (int i = 0; i < array.length - 1; i++) {
                    int id = array[i];
                    try {
                        //wtf is this?
                        if (mc.world.getEntityByID(id) == null) continue;
                    } catch (ConcurrentModificationException exception) {
                        return;
                    }
                    Entity e = mc.world.getEntityByID(id);
                    if (e != null && e.isDead) {
                        if ((mc.player.getDistance(e) < 20.0f & e != mc.player) && e instanceof EntityPlayer) {
                            for (int t = 0; t <= rand.nextInt(extra.getValue()); t++) {
                                Vec3d pos = new Vec3d(e.posX + rand.nextInt(2) - 1, e.posY + rand.nextInt(2) - 1, e.posZ + rand.nextInt(2) - 1);
                                PopupText popupText = new PopupText(ChatFormatting.ITALIC + "" + ChatFormatting.BOLD + "EZ", pos);
                                popTexts.add(popupText);
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException ignoredlel) {
            //rreee empty catch block
        }
    }


    class PopupText {
        private String displayName;
        private Vec3d pos;
        private boolean markedToRemove;
        private int color;
        private Timer timer;
        private double yIncrease;

        public PopupText(final String displayName, final Vec3d pos) {
            this.timer = new Timer();
            this.yIncrease = Math.random();
            while (this.yIncrease > 0.025 || this.yIncrease < 0.011) {
                this.yIncrease = Math.random();
            }
            this.timer.reset();
            this.setDisplayName(displayName);
            this.pos = pos;
            this.markedToRemove = false;
            if (!randomColor.getValue()) {
                this.color = ClickGui.getCurrentColor().getRGB();
            } else {
                this.color = Color.getHSBColor(rand.nextFloat(), 1.0F, 0.9F).getRGB();
            }
        }

        public void Update() {
            this.pos = this.pos.add(0.0, this.yIncrease, 0.0);
            if (this.timer.passedMs(1000)) {
                this.markedToRemove = true;
            }
        }

        public boolean isMarked() {
            return this.markedToRemove;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public void setDisplayName(final String displayName) {
            this.displayName = displayName;
        }

        public int getColor() {
            return this.color;
        }
    }
}
