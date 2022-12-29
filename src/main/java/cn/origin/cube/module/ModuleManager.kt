package cn.origin.cube.module

import cn.origin.cube.event.events.world.Render3DEvent
import cn.origin.cube.module.huds.*
import cn.origin.cube.module.modules.client.*
import cn.origin.cube.module.modules.combat.*
import cn.origin.cube.module.modules.combat.AutoCrystal.AutoCrystal
import cn.origin.cube.module.modules.function.*
import cn.origin.cube.module.modules.function.scaffold.Scaffold
import cn.origin.cube.module.modules.movement.*
import cn.origin.cube.module.modules.visual.*
import cn.origin.cube.module.modules.world.*

class ModuleManager {
    var allModuleList = ArrayList<AbstractModule>()
    var moduleList = ArrayList<Module>()
    var hudList = ArrayList<HudModule>()

    init {
        //Client
        registerModule(ClickGui())
        registerModule(HudEditor())
        registerModule(ChatSuffix())
        registerModule(Console())
        registerModule(AutoConfig())
        registerModule(MainMenuShader())
        registerModule(Test())

        //Combat
        registerModule(Surround())
        registerModule(KillAura())
        registerModule(AutoTote())
        registerModule(AutoArmor())
        registerModule(Replenish())
        registerModule(AutoBowRelease())
        registerModule(KotlinAura())
        registerModule(Criticals())
        registerModule(AutoCrystal())

        //Function
        registerModule(MiddleClick())
        registerModule(FakeKick())
        registerModule(NoRotate())
        registerModule(AntiKnockback())
        registerModule(FastEXP())
        registerModule(NoFall())
        registerModule(Scaffold())
        registerModule(PacketEXP())
        registerModule(AutoFrameDupe())
        registerModule(PearlAlert())
        registerModule(ChorusLag())
        registerModule(SilentChorus())
        registerModule(BoatPlace())

        //Movement
        registerModule(Sprint())
        registerModule(AutoWalk())
        registerModule(ReverseStep())
        registerModule(Step())
        registerModule(NoSlow())
        registerModule(PacketFly())
        registerModule(ElytraFly())
        registerModule(ConstFly())
        registerModule(Speed())

        //Visual
        registerModule(FullBright())
        registerModule(BlockHighlight())
        registerModule(NameTags())
        registerModule(Chams())
        registerModule(HoleESP())
        registerModule(NoRender())
        registerModule(Ruler())
        registerModule(ESP())
        registerModule(Crosshair())
        registerModule(SuperheroFX())
        registerModule(ShaderCharms())
        registerModule(ItemPhysics())
        registerModule(ChunkBorders())
        registerModule(Animations())

        //World
        registerModule(FakePlayer())
        registerModule(AutoRespawn())
        registerModule(Suicide())
        registerModule(AntiVoid())
        registerModule(FastPlace())
        registerModule(Rotator())
        registerModule(PacketMine())
        registerModule(ViewLock())
        registerModule(BigPOV())

        //Hud
        registerModule(WaterMark())
        registerModule(ModuleArrayList())
        registerModule(WelcomerHud())
        registerModule(ArmorHud())
        registerModule(InventoryHud())
        registerModule(CoordsHud())

    }

    private fun registerModule(module: AbstractModule) {
        if (!allModuleList.contains(module)) allModuleList.add(module)
        if (module.isHud) {
            if (!hudList.contains(module)) hudList.add(module as HudModule)
        } else if (!moduleList.contains(module)) {
            moduleList.add(module as Module)
        }
    }

    fun getModulesByCategory(category: Category): List<AbstractModule> {
        return allModuleList.filter { it.category == category }
    }

    fun getModuleByClass(clazz: Class<*>): AbstractModule? {
        for (abstractModule in allModuleList) {
            if (abstractModule::class.java == clazz) return abstractModule
        }
        return null
    }


    fun getModuleByName(name: String): AbstractModule? {
        for (abstractModule in allModuleList) {
            if (abstractModule.name.lowercase() == name.lowercase()) return abstractModule
        }
        return null
    }

    fun onUpdate() {
        allModuleList.filter { it.isEnabled }.forEach { it.onUpdate() }
    }

    fun onLogin() {
        allModuleList.filter { it.isEnabled }.forEach { it.onLogin() }
    }

    fun onLogout() {
        allModuleList.filter { it.isEnabled }.forEach { it.onLogout() }
    }

    fun onRender3D(event: Render3DEvent) {
        allModuleList.filter { it.isEnabled }.forEach { it.onRender3D(event) }
    }

    fun onRender2D() {
        allModuleList.filter { it.isEnabled }.forEach { it.onRender2D() }
    }
}