package br.com.cubeland.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {

    public static String translateColorCodes(String rawMessage) {
        String message = ChatColor.translateAlternateColorCodes('&', rawMessage);

        return message;
    }

}
