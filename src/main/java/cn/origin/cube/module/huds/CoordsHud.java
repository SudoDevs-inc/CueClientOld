package cn.origin.cube.module.huds;

import cn.origin.cube.Cube;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.HudModule;
import cn.origin.cube.module.HudModuleInfo;
import cn.origin.cube.module.modules.client.ClickGui;
import cn.origin.cube.settings.FloatSetting;
import org.lwjgl.opengl.GL11;

@HudModuleInfo(name = "Coords", x = 114, y = 114, descriptions = "Show hack name", category = Category.HUD)
public class CoordsHud extends HudModule {

    public FloatSetting Scala = registerSetting("Size", 1.0f, 0.0f, 3.0f);

    @Override
    public void onRender2D() {
        GL11.glPushMatrix();
        GL11.glTranslated(this.x, (float) this.y, 0);
        GL11.glScaled((double) this.Scala.getValue(), (double) this.Scala.getValue(), 0.0);
        String coords = "";
        switch (mc.player.world.provider.getDimensionType()) {
            case NETHER:
                coords = String.format("X:%s Y:%s Z:%s | OW: [X:%s Y:%s Z:%s]",
                        (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ,
                        (int) mc.player.posX * 8, (int) mc.player.posY * 8, (int) mc.player.posZ * 8);
                break;
            default:
                coords = String.format("X:%s Y:%s Z:%s | NETHER: [X:%s Y:%s Z:%s]",
                        (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ,
                        (int) mc.player.posX / 8, (int) mc.player.posY / 8, (int) mc.player.posZ / 8);
                break;
        }
        Cube.fontManager.CustomFont.drawString(coords, 0, 0, ClickGui.getCurrentColor().getRGB());
        GL11.glPopMatrix();
        this.width = (int) ((float) Cube.fontManager.CustomFont.getStringWidth("CueClient") * this.Scala.getValue());
        this.height = (int) ((float) Cube.fontManager.CustomFont.getHeight() * this.Scala.getValue());
    }
}
