package com.obyrkl.cryptominer.Listeners;

import com.obyrkl.cryptominer.Gui.MinerMainGui;
import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class MinerListeners implements Listener {

    @EventHandler
    public void MinerPlaceEvent(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(!event.getItemInHand().hasItemMeta()){
            return;
        }
        if(!event.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(MinerUtil.getMinerItem(null).getItemMeta().getDisplayName())
                || !event.getItemInHand().getType().equals(MinerUtil.getMinerItem(null).getType())){
            return;
        }
        UUID minerUUID = UUID.fromString(ChatColor.stripColor(event.getItemInHand().getItemMeta().getLore().get(0).split(": ")[1]));
        Miner miner = Main.instance.getMinerManager().getMiner(minerUUID);
        miner.setPlaced(true);
        miner.setLocation(event.getBlock().getLocation());
    }

    @EventHandler
    public void MinerBreakEvent(BlockBreakEvent event){
        if(Main.instance.getMinerManager().getMiner(event.getBlock().getLocation()) == null){
            return;
        }
        Miner miner = Main.instance.getMinerManager().getMiner(event.getBlock().getLocation());
        miner.setPlaced(false);
        miner.setLocation(new Location(Bukkit.getWorld("world"),0.0,0.0,0.0,0,0));
        event.setDropItems(false);
        event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(),MinerUtil.getMinerItem(miner.getUUID()));
    }

    @EventHandler
    public void MinerClickEvent(PlayerInteractEvent event){
        if(event.getClickedBlock() == null ||
                Main.instance.getMinerManager().getMiner(event.getClickedBlock().getLocation()) == null ||
                event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        MinerMainGui.MinerMainGui(event.getPlayer(),Main.instance.getMinerManager().getMiner(event.getClickedBlock().getLocation()));
    }

}
