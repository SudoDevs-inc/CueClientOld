package cn.origin.cube.event.events.player;

import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SwingArmEvent extends Event {

    private EnumHand hand;

    public SwingArmEvent(EnumHand hand) {
        this.hand = hand;
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public void setHand(EnumHand hand) {
        this.hand = hand;
    }
}