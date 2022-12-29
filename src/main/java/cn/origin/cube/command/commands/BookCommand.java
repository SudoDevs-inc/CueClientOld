package cn.origin.cube.command.commands;

import cn.origin.cube.command.Command;
import cn.origin.cube.command.CommandInfo;
import cn.origin.cube.utils.client.ChatUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CommandInfo(name = "book", aliases = {"b"}, descriptions = "Show command list", usage = "book")
public class BookCommand extends Command {

    @Override
    public void execute(String[] args) {
        ItemStack heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand ( );
        if ( heldItem.getItem ( ) == Items.WRITABLE_BOOK ) {
            int limit = 50;
            Random rand = new Random ( );
            IntStream characterGenerator = rand.ints ( 128 , 1112063 ).map (i -> i < 55296 ? i : i + 2048 );
            String joinedPages = characterGenerator.limit ( 10500L ).mapToObj ( i -> String.valueOf ( (char) i ) ).collect ( Collectors.joining ( ) );
            NBTTagList pages = new NBTTagList ( );
            for (int page = 0; page < 50; ++ page) {
                pages.appendTag ( new NBTTagString( joinedPages.substring ( page * 210 , ( page + 1 ) * 210 ) ) );
            }
            if ( heldItem.hasTagCompound ( ) ) {
                heldItem.getTagCompound ( ).setTag ( "pages" , pages );
            } else {
                heldItem.setTagInfo ( "pages" , pages );
            }
            StringBuilder stackName = new StringBuilder ( );
            for (int i2 = 0; i2 < 16; ++ i2) {
                stackName.append ( "\u0014\f" );
            }
            heldItem.setTagInfo ( "author" , new NBTTagString ( Minecraft.getMinecraft().player.getName ( ) ) );
            heldItem.setTagInfo ( "title" , new NBTTagString ( stackName.toString ( ) ) );
            PacketBuffer buf = new PacketBuffer ( Unpooled.buffer ( ) );
            buf.writeItemStack ( heldItem );
            Minecraft.getMinecraft().player.connection.sendPacket ( new CPacketCustomPayload( "MC|BSign" , buf ) );

            ChatUtil.sendNoSpamColoredMessage("Done writing book");
        } else {
            ChatUtil.sendNoSpamColoredMessage("Error: " + this.aliases);
        }
    }
}
