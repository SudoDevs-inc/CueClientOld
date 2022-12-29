package cn.origin.cube.module.modules.world

import cn.origin.cube.module.Category
import cn.origin.cube.module.Module
import cn.origin.cube.module.ModuleInfo
import cn.origin.cube.settings.FloatSetting
import cn.origin.cube.settings.ModeSetting

@ModuleInfo(name = "ViewLock",
    descriptions = "ViewLock",
    category = Category.WORLD)
class ViewLock: Module() {
    private val pitch: FloatSetting = registerSetting("Pitch",  1f, -90f, 90f)
    val yaw: FloatSetting = registerSetting("Yaw", 1f, 0f, 8f)
    val mode: ModeSetting<Mode> = registerSetting("Mode", Mode.Yaw)

    override fun onUpdate() {
        if (mode.value == Mode.Yaw || mode.value == Mode.Both) {
            mc.player.rotationYaw = yaw.value * 45f
        }
        if (mode.value == Mode.Pitch || mode.value == Mode.Both) {
            mc.player.rotationPitch = pitch.value
        }
    }

    enum class Mode {
        Both, Yaw, Pitch
    }
}