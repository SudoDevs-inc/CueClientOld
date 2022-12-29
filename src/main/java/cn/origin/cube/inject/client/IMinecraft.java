package cn.origin.cube.inject.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface IMinecraft {

    @Accessor("rightClickDelayTimer")
    void hookSetRightClickDelayTimer(int newTimer);

    @Accessor("timer")
    Timer hookGetTimer();

    @Accessor("session")
    Session hookGetSession();

    @Accessor("session")
    void hookSetSession(Session newSession);

}
