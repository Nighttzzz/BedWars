package br.com.cubeland.utils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MessagesUtil {

    public static String translateColorCodes(String rawMessage) {
        String message = ChatColor.translateAlternateColorCodes('&', rawMessage);

        return message;
    }

    public void sendActionBar(Player player, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void broadcastActionBar(String text) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendActionBar(player, text);
        }
    }

}
