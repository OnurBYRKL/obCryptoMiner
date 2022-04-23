package com.obyrkl.cryptominer.Utils;

import org.bukkit.ChatColor;

public class ChatUtil {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
