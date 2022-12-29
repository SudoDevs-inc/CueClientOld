package cn.origin.cube.event.events.player;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TotemPopEvent extends Event {
    public Entity entity;
    public int popCount;

    public TotemPopEvent(Entity entity, int popCount) {
        this.entity = entity;
        this.popCount = popCount;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getPopCount() {
        return popCount;
    }
}
