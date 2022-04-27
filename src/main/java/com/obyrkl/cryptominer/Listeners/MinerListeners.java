package com.obyrkl.cryptominer.Listeners;

import com.obyrkl.cryptominer.Gui.MinerMainGui;
import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.ChatUtil;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
        if(miner == null){
            event.setCancelled(true);
            player.setItemInHand(new ItemStack(Material.AIR));
            player.sendMessage(ChatUtil.format(Main.instance.getLang().getString("Messages.minerDeletedSystem")));
            return;
        }
        miner.setPlaced(true);
        miner.setLastUsed(System.currentTimeMillis());
        miner.setOwner(player.getUniqueId());
        miner.setLocation(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void MinerBreakEvent(BlockBreakEvent event){
        if(Main.instance.getMinerManager().getMiner(event.getBlock().getLocation()) == null){
            return;
        }
        Miner miner = Main.instance.getMinerManager().getMiner(event.getBlock().getLocation());
        if(!miner.getOwner().equals(event.getPlayer().getUniqueId())){
            event.getPlayer().sendMessage(ChatUtil.format(Main.instance.getLang().getString("Messages.noBreakAllowed")));
            event.setCancelled(true);
            return;
        }
        miner.setPlaced(false);
        miner.setLastUsed(System.currentTimeMillis());
        miner.setLocation(new Location(Bukkit.getWorld("world"),0.0,0.0,0.0,0,0));
        event.setDropItems(false);
        event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(),MinerUtil.getMinerItem(miner.getUUID()));
    }

    @EventHandler
    public void MinerClickEvent(PlayerInteractEvent event){
        if(event.getClickedBlock() == null ||
                Main.instance.getMinerManager().getMiner(event.getClickedBlock().getLocation()) == null ||
                event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                event.getHand() != EquipmentSlot.HAND){
            return;
        }
        if(event.getItem() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(MinerUtil.getMinerItem(null).getItemMeta().getDisplayName())
                && event.getItem().getType().equals(MinerUtil.getMinerItem(null).getType())){
            return;
        }
        Miner miner = Main.instance.getMinerManager().getMiner(event.getClickedBlock().getLocation());
        if(!miner.getOwner().equals(event.getPlayer().getUniqueId())){
            event.getPlayer().sendMessage(ChatUtil.format(Main.instance.getLang().getString("Messages.noManageAllowed")));
            return;
        }
        if(event.getPlayer().isSneaking()){
            if(miner.getDurability() <= 0){
                event.getPlayer().sendMessage(ChatUtil.format(Main.instance.getLang().getString("Messages.minerFailedStart")));
                return;
            }
            if(miner.isMinerEnabled() == true){
                miner.setMinerStatus(false);
                event.getPlayer().playSound(event.getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, (float) 0.1);
            }else{
                miner.setMinerStatus(true);
                event.getPlayer().playSound(event.getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1, 1);
            }
            String message = Main.instance.getLang().getString("Messages.minerStatusChanged");
            message = message.replace("%status%",((miner.isMinerEnabled() == true) ? Main.instance.getLang().getString("Messages.active") : Main.instance.getLang().getString("Messages.deactive")));
            event.getPlayer().sendMessage(ChatUtil.format(message));
            return;
        }
        MinerMainGui.MinerMainGui(event.getPlayer(),Main.instance.getMinerManager().getMiner(event.getClickedBlock().getLocation()));
    }
}
