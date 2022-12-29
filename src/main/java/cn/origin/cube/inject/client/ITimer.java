package cn.origin.cube.inject.client;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface ITimer {

    @Accessor("tickLength")
    float hookGetTickLength();

    @Accessor("tickLength")
    void hookSetTickLength(float ticks);

}
