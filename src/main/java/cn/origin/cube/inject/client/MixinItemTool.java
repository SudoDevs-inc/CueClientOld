package cn.origin.cube.inject.client;

import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTool.class)
public abstract class MixinItemTool implements IItemTool {

    @Accessor(value = "attackDamage")
    public abstract float getAttackDamage();

}
