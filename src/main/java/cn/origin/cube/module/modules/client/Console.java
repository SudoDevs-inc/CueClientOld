package cn.origin.cube.module.modules.client;

import cn.origin.cube.guis.console.GuiConsole;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Console", descriptions = "open Console screen", category = Category.CLIENT, defaultKeyBind = Keyboard.KEY_P)
public class Console extends Module {

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new GuiConsole());
        disable();
        super.onEnable();
    }
}
