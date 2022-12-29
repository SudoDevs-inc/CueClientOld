package cn.origin.cube.module.modules.combat;

import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.module.modules.client.AutoConfig;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.DoubleSetting;
import cn.origin.cube.utils.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "Replenish",
        descriptions = "Automatically replenishes your health",
        category = Category.COMBAT)
public class Replenish extends Module {

    DoubleSetting percent = registerSetting("Percent", 1.0, 1.0, 99.0);
    DoubleSetting delay = registerSetting("Delay", 100.0, 100.0, 1000.0);
    BooleanSetting wait = registerSetting("Wait", false);

    private final Map<Integer, ItemStack> hotbar = new ConcurrentHashMap<>();

    private final Timer timer = new Timer();

    private int refillSlot = -1;

    @Override
    public void onDisable() {
        super.onDisable();

        // reset values
        hotbar.clear();
        refillSlot = -1;
    }

    @Override
    public void onUpdate() {
        if(fullNullCheck())return;

        if (refillSlot == -1) {

            for (int i = 0; i < 9; ++i) {

                ItemStack stack = mc.player.inventory.getStackInSlot(i);

                if (hotbar.getOrDefault(i, null) == null) {

                    if (stack.getItem().equals(Items.AIR)) {
                        continue;
                    }

                    hotbar.put(i, stack);
                    continue;
                }

                double percentage = ((double) stack.getCount() / (double) stack.getMaxStackSize()) * 100.0;

                if (percentage <= percent.getValue()) {

                    if (stack.getItem().equals(Items.END_CRYSTAL) && wait.getValue()) {
                        continue;
                    }

                    if (!timer.passed(delay.getValue().longValue())) {

                        refillSlot = i;
                    }

                    else {

                        fillStack(i, stack);

                        timer.reset();
                    }

                    break;
                }
            }
        }

        else {
            if (timer.passed(delay.getValue().longValue())) {

                fillStack(refillSlot, hotbar.get(refillSlot));

                timer.reset();
                refillSlot = -1;
            }
        }
    }

    private void fillStack(int slot, ItemStack stack) {

        if (slot != -1 && stack != null) {
            int replenishSlot = -1;

            for (int i = 9; i < 36; ++i) {
                ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

                if (!itemStack.isEmpty()) {

                    if (!stack.getDisplayName().equals(itemStack.getDisplayName())) {
                        continue;
                    }

                    if (stack.getItem() instanceof ItemBlock) {
                        if (!(itemStack.getItem() instanceof ItemBlock)) {
                            continue;
                        }

                        ItemBlock hotbarBlock = (ItemBlock) stack.getItem();
                        ItemBlock inventoryBlock = (ItemBlock) itemStack.getItem();

                        if (!hotbarBlock.getBlock().equals(inventoryBlock.getBlock())) {
                            continue;
                        }
                    }

                    else {
                        if (!stack.getItem().equals(itemStack.getItem())) {
                            continue;
                        }
                    }

                    replenishSlot = i;
                }
            }

            if (replenishSlot != -1) {

                int total = stack.getCount() + mc.player.inventory.getStackInSlot(replenishSlot).getCount();

                mc.playerController.windowClick(0, replenishSlot, 0, ClickType.PICKUP, mc.player);

                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);

                if (total >= stack.getMaxStackSize()) {
                    mc.playerController.windowClick(0, replenishSlot, 0, ClickType.PICKUP, mc.player);
                }

                refillSlot = -1;
            }
        }
    }

    public static Replenish INSTANCE;

    public Replenish() {
        INSTANCE = this;
    }
}
