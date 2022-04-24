package com.obyrkl.cryptominer.Listeners.Gui;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.ChatUtil;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class MainGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equalsIgnoreCase(ChatUtil.format("&0Miner"))) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        Player player = (Player) e.getWhoClicked();
        if(Main.instance.getMinerManager().getOpenedMiner(player.getUniqueId()) == null){
            player.closeInventory();
            return;
        }
        Miner miner = Main.instance.getMinerManager().getOpenedMiner(player.getUniqueId());
        if (e.getRawSlot() == 16) {
            player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            if(e.getClick() == ClickType.LEFT){
                new AnvilGUI.Builder()
                    .onComplete((player1, text) -> {
                        if(!MinerUtil.isInteger(text) && !MinerUtil.isDouble(text)){
                            player1.sendMessage(ChatUtil.format(Main.instance.getConfig().getString("Messages.wrongNumberEntry")));
                            return AnvilGUI.Response.close();
                        }
                        double count = Double.parseDouble(text);
                        if(miner.getMinerBalance() <= 0 || miner.getMinerBalance() < count){
                            player1.sendMessage(ChatUtil.format(Main.instance.getConfig().getString("Messages.insufficientBalance")));
                            return AnvilGUI.Response.close();
                        }
                        double price = Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice()*count;
                        miner.setBalance(miner.getMinerBalance()-count);
                        Main.instance.getEconomy().depositPlayer(player1,price);
                        String message = Main.instance.getConfig().getString("Messages.cryptocurrencySold");
                        message = message.replace("%amount%",String.format("%.6f", count));
                        message = message.replace("%crypto%","Bitcoin");
                        message = message.replace("%price%",String.format("%.2f", price));
                        player1.sendMessage(ChatUtil.format(message));
                        return AnvilGUI.Response.close();
                    })
                    .text("örn: '"+String.format("%.6f", miner.getMinerBalance())+"'")
                    .itemLeft(new ItemStack(Material.IRON_SWORD))
                    .itemRight(new ItemStack(Material.IRON_SWORD))
                    .title(Main.instance.getConfig().getString("Messages.inputAmount"))
                    .plugin(Main.instance)
                    .open(player);
            }else if(e.getClick() == ClickType.SHIFT_LEFT){
                player.closeInventory();
                double count = miner.getMinerBalance();
                if(miner.getMinerBalance() <= 0){
                    player.sendMessage(ChatUtil.format(Main.instance.getConfig().getString("Messages.insufficientBalance")));
                    return;
                }
                double price = Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice()*count;
                miner.setBalance(miner.getMinerBalance()-count);
                Main.instance.getEconomy().depositPlayer(player,price);
                String message = Main.instance.getConfig().getString("Messages.cryptocurrencySold");
                message = message.replace("%amount%",String.format("%.6f", count));
                message = message.replace("%crypto%","Bitcoin");
                message = message.replace("%price%",String.format("%.2f", price));
                player.sendMessage(ChatUtil.format(message));
            }
        }else if (e.getRawSlot() == 10) {
            player.playSound(player.getEyeLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            if(e.getClick() == ClickType.LEFT){
                player.closeInventory();
                if(miner.getDurability() <= 0){
                    player.sendMessage(ChatUtil.format(Main.instance.getConfig().getString("Messages.minerFailedStart")));
                    return;
                }
                if(miner.isMinerEnabled() == true){
                    miner.setMinerStatus(false);
                }else{
                    miner.setMinerStatus(true);
                }
                String message = Main.instance.getConfig().getString("Messages.minerStatusChanged");
                message = message.replace("%status%",((miner.isMinerEnabled() == true) ? Main.instance.getConfig().getString("Messages.active") : Main.instance.getConfig().getString("Messages.deactive")));
                player.sendMessage(ChatUtil.format(message));
            }else if(e.getClick() == ClickType.RIGHT){
                player.closeInventory();
                if(miner.getDurability() > 0){
                    return;
                }
                double repairCost = Main.instance.getConfig().getDouble("Miner.repairCost");
                if(Main.instance.getEconomy().getBalance(player) < repairCost){
                    player.sendMessage(ChatUtil.format(Main.instance.getConfig().getString("Messages.insufficientBalance")));
                    return;
                }
                Main.instance.getEconomy().withdrawPlayer(player,repairCost);
                miner.setDurability(100.0);
                String message = Main.instance.getConfig().getString("Messages.minerRepairSuccessful");
                message = message.replace("%price%",String.format("%.2f", repairCost));
                player.sendMessage(ChatUtil.format(message));
            }
        }
    }

}
