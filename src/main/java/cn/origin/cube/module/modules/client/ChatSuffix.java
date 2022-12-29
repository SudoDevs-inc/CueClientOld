package cn.origin.cube.module.modules.client;

import cn.origin.cube.Cube;
import cn.origin.cube.event.events.client.PacketEvent;
import cn.origin.cube.module.Category;
import cn.origin.cube.module.Module;
import cn.origin.cube.module.ModuleInfo;
import cn.origin.cube.settings.BooleanSetting;
import cn.origin.cube.settings.ModeSetting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "ChatSuffix", descriptions = "Chat suffix", category = Category.CLIENT)
public class ChatSuffix extends Module {

    BooleanSetting version = registerSetting("Version", false);
    BooleanSetting strict = registerSetting("Strict", false);
    ModeSetting<AppendMode> mode = registerSetting("Strict", AppendMode.Line);
    // modify these to your liking
    private static final String UNICODE = toUnicode(Cube.MOD_NAME);

    private static final String NORMAL = Cube.MOD_NAME;

    @Override
    public void onEnable() {
        if (fullNullCheck())
            return;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientChat(ClientChatEvent event) {
        if (event.isCanceled() || event.getMessage().startsWith("/"))
            return;
        if (event.isCanceled() || event.getMessage().startsWith("."))
            return;
        if (event.isCanceled() || event.getMessage().startsWith("*"))
            return;
        event.setMessage(event.getMessage() + " " + mode.getValue().getString() + " " + (strict.getValue() ? NORMAL : UNICODE));
    }

    private enum AppendMode {
        Line("|"),
        Arrows(">>"),
        Colon(":"),
        HashTag("#");

        private final String string;

        AppendMode(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    private static String toUnicode(String message) {
        return message.toLowerCase()
                .replace("a", "\u1d00")
                .replace("b", "\u0299")
                .replace("c", "\u1d04")
                .replace("d", "\u1d05")
                .replace("e", "\u1d07")
                .replace("f", "\ua730")
                .replace("g", "\u0262")
                .replace("h", "\u029c")
                .replace("i", "\u026a")
                .replace("j", "\u1d0a")
                .replace("k", "\u1d0b")
                .replace("l", "\u029f")
                .replace("m", "\u1d0d")
                .replace("n", "\u0274")
                .replace("o", "\u1d0f")
                .replace("p", "\u1d18")
                .replace("q", "\u01eb")
                .replace("r", "\u0280")
                .replace("s", "\ua731")
                .replace("t", "\u1d1b")
                .replace("u", "\u1d1c")
                .replace("v", "\u1d20")
                .replace("w", "\u1d21")
                .replace("x", "\u02e3")
                .replace("y", "\u028f")
                .replace("z", "\u1d22");
    }
}