package cn.origin.cube.module.modules.client;

import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.combat.AutoCrystal.AutoCrystal;
import cn.origin.cube.module.modules.combat.Replenish;
import cn.origin.cube.module.modules.movement.NoSlow;
import cn.origin.cube.settings.ModeSetting;

//ToDo finish this shit
@ModuleInfo(name = "AutoConfig", descriptions = "AutoConfig", category = Category.CLIENT)
public class AutoConfig extends Module {

    ModeSetting<Server> server = registerSetting("Server", Server.TwoBee);

    @Override
    public void onEnable() {
        if(server.getValue().equals(Server.TwoBee)){
            //NoSlow
            NoSlow.INSTANCE.enable();
            NoSlow.INSTANCE.strict.setValue(true);

            //Replenish
            Replenish.INSTANCE.enable();

            //AC
            AutoCrystal.INSTANCE.switchToCrystal.setValue(false);
            AutoCrystal.INSTANCE.players.setValue(true);
            AutoCrystal.INSTANCE.mobs.setValue(false);
            AutoCrystal.INSTANCE.passives.setValue(false);
            AutoCrystal.INSTANCE.place.setValue(true);
            AutoCrystal.INSTANCE.explode.setValue(true);
            AutoCrystal.INSTANCE.range.setValue(5);
            AutoCrystal.INSTANCE.minDamage.setValue(6);
            AutoCrystal.INSTANCE.selfDamage.setValue(12);
            AutoCrystal.INSTANCE.antiWeakness.setValue(false);
            AutoCrystal.INSTANCE.multiPlace.setValue(false);
            AutoCrystal.INSTANCE.rotate.setValue(true);
            AutoCrystal.INSTANCE.autoTimerl.setValue(false);
            AutoCrystal.INSTANCE.rayTrace.setValue(false);
            AutoCrystal.INSTANCE.breakSpeed.setValue(10);
            AutoCrystal.INSTANCE.placeSpeed.setValue(10);
            AutoCrystal.INSTANCE.thinking.setValue(true);
            AutoCrystal.INSTANCE.cancelCrystal.setValue(false);
        }
        super.onEnable();
    }

    public static AutoConfig INSTANCE;

    public AutoConfig() {
        INSTANCE = this;
    }

    public enum Server{
        TwoBee,pvpdotcc,NeinBee
    }
}
