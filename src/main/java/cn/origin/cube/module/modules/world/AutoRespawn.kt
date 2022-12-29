package cn.origin.cube.module.modules.world

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import net.minecraft.client.gui.GuiGameOver

@ModuleInfo(name = "AutoRespawn",
    descriptions = "Anti Death Screen",
    category = Category.WORLD)
class AutoRespawn:Module() {
    override fun onUpdate() {
        if (mc.currentScreen is GuiGameOver && mc.player.getHealth() >= 0.0f) {
            mc.player.respawnPlayer()
            mc.displayGuiScreen(null)
        }
    }
}