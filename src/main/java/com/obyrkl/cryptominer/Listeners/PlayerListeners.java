package com.obyrkl.cryptominer.Listeners;

import com.obyrkl.cryptominer.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (Main.instance.getMinerManager().hasOpenedMiner(player.getUniqueId())) {
            Main.instance.getMinerManager().minerClosed(player.getUniqueId());
        }
    }

}
