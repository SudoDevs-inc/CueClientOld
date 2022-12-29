package cn.origin.cube.module.huds;

import cn.origin.cube.Cube;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.HudModule;
import cn.origin.cube.module.HudModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.FloatSetting;
import cn.origin.cube.utils.render.Render2DUtil;
import cn.origin.cube.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@HudModuleInfo(name = "Inventory", descriptions = "Show all enable module", category = Category.HUD, y = 200, x = 100)
public class InventoryHud extends HudModule {

    public FloatSetting Scala = registerSetting("Size", 1.0f, 0.0f, 10.0f);

    @Override
    public void onRender2D(){

        GL11.glPushMatrix();
        GL11.glTranslated(this.x, (float) this.y, 0);
        GL11.glScaled((double) this.Scala.getValue(), (double) this.Scala.getValue(), 0.0);
        RenderHelper.enableGUIStandardItemLighting();
        Render2DUtil.drawRect1(x - 2, y - 2, x - 1, y + 58, new Color(64,41,213).getRGB());
        Render2DUtil.drawRect1(x + 177, y - 1, x + 178, y+ 57, new Color(64,41,213, 255).getRGB());
        Render2DUtil.drawRect1(x + 177, y - 2, x + 178, y + 58, new Color(64,41,213, 255).getRGB());
        Render2DUtil.drawRect1(x -2, y - 3, x + 178, y - 2, new Color(64,41,213,255).getRGB());
        Render2DUtil.drawRect1(x - 1, y + 57, x + 177, y + 58, new Color(64,41,213,255).getRGB());
        Render2DUtil.drawRect1(x - 1, y - 2, x + 177, y + 57, new Color(10,10,10,155).getRGB());
        for (int i = 0; i < 27; i++)
        {
            ItemStack item_stack = Minecraft.getMinecraft().player.inventory.mainInventory.get(i + 9);
            int item_position_x = (int) x + (i % 9) * 20;
            int item_position_y = (int) y + (i / 9) * 20;
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item_stack, item_position_x, item_position_y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, item_stack, item_position_x, item_position_y, null);
        }
        Minecraft.getMinecraft().getRenderItem().zLevel = - 5.0f;
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        this.width = (int) ((float) Cube.fontManager.CustomFont.getStringWidth("CueClient") * this.Scala.getValue());
        this.height = (int) ((float) Cube.fontManager.CustomFont.getHeight() * this.Scala.getValue());
    }
}
