package cn.origin.cube.module.modules.visual;

import cn.origin.cube.Cube;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.DoubleSetting;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.settings.IntegerSetting;
import cn.origin.cube.utils.client.MathUtil;
import cn.origin.cube.utils.render.Render2DUtil;
import cn.origin.cube.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import net.minecraft.enchantment.*;
import net.minecraft.init.*;

import java.awt.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.player.*;

import java.util.*;
import java.util.List;

import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "NameTags", descriptions = "Always light", category = Category.VISUAL)
public class NameTags extends Module {
    public static NameTags INSTANCE;
    BooleanSetting mutiThread = registerSetting("MutliThread", true);
    BooleanSetting cFont = registerSetting("CFont", false);
    FloatSetting cfontOffsetY = registerSetting("Y Offset", 1, -2.0F, 2.0F).booleanVisible(cFont);
    FloatSetting cfontOffsetX = registerSetting("X Offset", 0, -2.0F, 2.0F).booleanVisible(cFont);
    BooleanSetting armor = registerSetting("Armor", true);
    BooleanSetting items = registerSetting("Items", true);
    BooleanSetting heart = registerSetting("Heart", false);
    BooleanSetting sneak = registerSetting("Sneak", false);
    FloatSetting sneakHeight = registerSetting("Sneak Height", -1f, -4f, 1f).booleanVisible(sneak);
    BooleanSetting enchant = registerSetting("Enchantments", false);
    BooleanSetting healthBar = registerSetting("Health Bar", false);
    BooleanSetting background = registerSetting("Background", true);
    BooleanSetting roundedOutline = registerSetting("RoundedBackground", true).booleanDisVisible(background);
    DoubleSetting radius = registerSetting("Radius", 1.0, 1, 5).booleanDisVisible(background);
    BooleanSetting gradientBackground = registerSetting("Gradient Background", true).booleanVisible(background);
    FloatSetting outlineWidth = registerSetting("Outline Width", 1.0f, 0f, 3.0f);
    FloatSetting yOffset = registerSetting("Y Offset", 0, -10.0F, 10.0F);
    FloatSetting sizel = registerSetting("Size", 1.3F, -10.0F, 10.0F);
    IntegerSetting width = registerSetting("Width", 0, -5, 5);
    IntegerSetting height = registerSetting("Height", 0, -5, 5);
    public List<EntityPlayer> entityPlayers = new ArrayList<>();

    public NameTags() {
        INSTANCE = this;
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (fullNullCheck()) return;
        for (Entity e : NameTags.mc.world.loadedEntityList) {
            if (!(e instanceof EntityPlayer) || e == NameTags.mc.player) continue;
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)event.getPartialTicks() - NameTags.mc.getRenderManager().viewerPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)event.getPartialTicks() - NameTags.mc.getRenderManager().viewerPosY - yOffset.getValue() + (sneak.getValue() ? sneakHeight.getValue() : 0);
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)event.getPartialTicks() - NameTags.mc.getRenderManager().viewerPosZ;
            GL11.glPushMatrix();
            GL11.glDisable((int)2929);
            GL11.glDisable((int)3553);
            GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            float size = Math.min(Math.max(1.2f * (NameTags.mc.player.getDistance(e) * 0.15f), 1.25f), 6.0f) * 0.015f * sizel.getValue();
            GL11.glTranslatef((float)((float)x), (float)((float)y + e.height + 0.6f), (float)((float)z));
            GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.rotate((float)(-NameTags.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.rotate((float)NameTags.mc.getRenderManager().playerViewX, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glScalef((float)(-size), (float)(-size), (float)(-size));
            int health = (int)(((EntityPlayer)e).getHealth() / ((EntityPlayer)e).getMaxHealth() * 100.0f);
            if(background.getValue()) {
                if(gradientBackground.getValue()) {
                    Render2DUtil.drawGradientBorderedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), (int) -2 - height.getValue(), (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16) + width.getValue(), (int) 10 + height.getValue(), new Color(25, 25, 25, 150).getRGB());
                }else{
                    Render2DUtil.drawBorderedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), (int) -2 - height.getValue(), (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16)  + width.getValue(), (int) 10 + height.getValue(), outlineWidth.getValue(), (background.getValue() ? new Color(25,25,25,150).getRGB() :  new Color(25,25,25,0).getRGB()), ClickGui.getCurrentColor().getRGB());
                }

            }else if(roundedOutline.getValue()){
                drawRoundedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), (int) -2 - height.getValue(), (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16) + width.getValue(), (int) 10 + height.getValue(), new Color(25, 25, 25, 150).getRGB(), radius.getValue().intValue());
                drawOutlineRoundedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), (int) -2 - height.getValue(), (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16) + width.getValue(), (int) 10 + height.getValue(), ClickGui.getCurrentColor().getRGB(),radius.getValue().intValue(), mc.player.getDistance(e), size);
            }else {
                Render2DUtil.drawBorderedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), (int) -2 - height.getValue(), (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16)  + width.getValue(), (int) 10 + height.getValue(), outlineWidth.getValue(),new Color(25,25,25,0).getRGB(), ClickGui.getCurrentColor().getRGB());
            }
//            if (healthBar.getValue()) {
//                int length = (int) (((NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") * 2 - 1) * health));
//                length = Math.max(2, length);
//                Color color = new Color(0xBB0A0A);
//                GlStateManager.disableDepth();
//                drawRoundedRect((int) (-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), 4, (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16)  + width.getValue() - length, 6, color.getRGB(), 1);
//            }
            if(cFont.getValue()) {
                Cube.fontManager.CustomFont.drawStringWithShadow(e.getName() + " " + (Object) TextFormatting.GREEN + health + (heart.getValue() ? "\u2764" : "%"), 0 - this.getcenter(e.getName() + " " + (Object) TextFormatting.GREEN + health + "%") + cfontOffsetX.getValue(), 2, -1);
            }else{
                mc.fontRenderer.drawStringWithShadow(e.getName() + " " + (Object) TextFormatting.GREEN + health + (heart.getValue() ? "\u2764" : "%"), 0 - this.getcenter(e.getName() + " " + (Object) TextFormatting.GREEN + health + "%"), 1, -1);
            }
            int posX = -NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 - 8;
            if (healthBar.getValue()) {Render2DUtil.drawLine((-NameTags.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2) - width.getValue(), (int) 11 + height.getValue(), (int) (NameTags.mc.fontRenderer.getStringWidth(e.getName()) / 2 + 16)  + width.getValue() + ( -health), (int) 11 + height.getValue(), 3, new Color(0, 255,0));}
            if(items.getValue()) {
                if (Item.getIdFromItem((Item) ((EntityPlayer) e).inventory.getCurrentItem().getItem()) != 0) {
                    NameTags.mc.getRenderItem().zLevel = -100.0f;
                    mc.getRenderItem().renderItemIntoGUI(new ItemStack(((EntityPlayer) e).inventory.getCurrentItem().getItem()), posX - 2, -20);
                    NameTags.mc.getRenderItem().zLevel = 0.0f;
                    int posY = -30;
                    Map enchantments = EnchantmentHelper.getEnchantments((ItemStack) ((EntityPlayer) e).inventory.getCurrentItem());
                    for (Object enchantment : enchantments.keySet()) {
                        if(enchant.getValue()) {
                            int level = EnchantmentHelper.getEnchantmentLevel((Enchantment) enchantment, (ItemStack) ((EntityPlayer) e).inventory.getCurrentItem());
                            mc.fontRenderer.drawStringWithShadow(String.valueOf(((Enchantment) enchantment).getName().substring(12).charAt(0)).toUpperCase() + level, (float) (posX + 6 - this.getcenter(String.valueOf(((Enchantment) enchantment).getName().substring(12).charAt(0)).toUpperCase() + level)), (float) posY, -1);
                            posY -= 12;
                        }
                    }
                    posX += 15;
                }
            }
            for (ItemStack item : e.getArmorInventoryList()) {
                if(armor.getValue()) {
                    NameTags.mc.getRenderItem().zLevel = -100.0f;
                    mc.getRenderItem().renderItemIntoGUI(new ItemStack(item.getItem()), posX, -20);
                    NameTags.mc.getRenderItem().zLevel = 0.0f;
                    int posY = -30;
                    Map enchantments = EnchantmentHelper.getEnchantments((ItemStack) item);
                    for (Object enchantment : enchantments.keySet()) {
                        if(enchant.getValue()) {
                            int level = EnchantmentHelper.getEnchantmentLevel((Enchantment) enchantment, (ItemStack) item);
                            mc.fontRenderer.drawStringWithShadow(String.valueOf(((Enchantment) enchantment).getName().substring(12).charAt(0)).toUpperCase() + level, (float) (posX + 9 - this.getcenter(((Enchantment) enchantment).getName().substring(12).charAt(0) + level)), (float) posY, -1);
                            posY -= 12;
                        }
                    }
                    posX += 17;
                }
            }
            int gapples = 0;
            if (Item.getIdFromItem((Item) ((EntityPlayer) e).inventory.getCurrentItem().getItem()) == 322) {
                gapples = ((EntityPlayer) e).inventory.getCurrentItem().getCount();
            } else if (Item.getIdFromItem((Item) ((EntityPlayer) e).getHeldItemOffhand().getItem()) == 322) {
                gapples = ((EntityPlayer) e).getHeldItemOffhand().getCount();
            }
            if (gapples > 0) {
                NameTags.mc.getRenderItem().zLevel = -100.0f;
                mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.GOLDEN_APPLE), posX, -20);
                NameTags.mc.getRenderItem().zLevel = 0.0f;
                mc.fontRenderer.drawStringWithShadow(String.valueOf(gapples), (float) (posX + 9 - this.getcenter(String.valueOf(gapples))), -30.0f, -1);
            }

            GL11.glEnable((int)2929);
            GL11.glPopMatrix();
        }
    }

    public int getcenter(String text) {
        return NameTags.mc.fontRenderer.getStringWidth(text) / 2;
    }

    public int getcenter(int text) {
        return NameTags.mc.fontRenderer.getStringWidth(String.valueOf(text)) / 2;
    }

    public void drawRoundedRect(int x, int y, int right, int bottom, int color, int radius) {
        GlStateManager.pushMatrix();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        glEnable(GL_LINE_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glBegin(GL_TRIANGLE_FAN);
        drawFilledCircle(right - radius, bottom - radius, radius, color, 0, 90);
        drawFilledCircle(right - radius, y + radius, radius, color, 90, 180);
        drawFilledCircle(x + radius, y + radius, radius, color, 180, 270);
        drawFilledCircle(x + radius, bottom - radius, radius, color, 270, 360);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void drawOutlineRoundedRect(int x, int y, int right, int bottom, int color, int radius, double distance, double distanceScale) {


        double divisor = MathUtil.lerp(MathHelper.clamp(distance * (sizel.maxValue + 1 - sizel.getValue()), 1, 90), MathHelper.clamp((sizel.maxValue + 1 - sizel.getValue()) * 2, 1, distance), sizel.getValue());


        RenderUtil.drawPolygonOutline(0, 90, (int) (360 / divisor), x, y, radius, 1f, color);
        RenderUtil.drawPolygonOutline(90, 180, (int) (360 / divisor), right-radius*2, y, radius, 1f, color);
        RenderUtil.drawPolygonOutline(180, 270, (int) (360 / divisor), right-radius*2, bottom-radius*2, radius, 1f, color);
        RenderUtil.drawPolygonOutline(270, 360, (int) (360 / divisor), x , bottom-radius*2, radius, 1f, color);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth((float) 1);

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x+radius,y, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right-radius,y, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right,y+radius, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right,bottom-radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x+radius,bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right-radius,bottom, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x,y+radius, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x,bottom-radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawFilledCircle(int x, int y, double radius, int color, int start, int stop) {
        glColor4f(((color >> 16) & 0xff) / 255F, ((color >> 8) & 0xff) / 255F, (color & 0xff) / 255F, ((color >> 24) & 0xff) / 255F);
        for (int i = start; i <= stop; i++)
            glVertex2d( x + Math.sin(((i * Math.PI) / 180)) * radius, y + Math.cos(((i * Math.PI) / 180)) * radius);
    }
}
