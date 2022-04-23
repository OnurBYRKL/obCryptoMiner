package com.obyrkl.cryptominer.Commands;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.ChatUtil;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length < 1){
                UUID minerUUID = MinerUtil.createRandomUUID();
                player.getInventory().addItem(MinerUtil.getMinerItem(minerUUID));
                Main.instance.getMinerManager().createMiner(player.getUniqueId(),minerUUID);
                player.sendMessage("item verildi!");
                return true;
            }else{
                if(args[0].equalsIgnoreCase("minerlist")){
                    for(Miner miner : Main.instance.getMinerManager().getMiners()){
                        player.sendMessage("-----------------------------------------------");
                        player.sendMessage(ChatUtil.format("&7UUID: &8"+miner.getUUID()));
                        player.sendMessage(ChatUtil.format("&7Owner: &8"+miner.getOwner()));
                        player.sendMessage(ChatUtil.format("&7Mine Value: &8"+String.format("%.6f", miner.getMineValue())));
                        player.sendMessage(ChatUtil.format("&7Mine Total Value: &8"+String.format("%.6f", miner.getTotalMineValue())));
                        player.sendMessage(ChatUtil.format("&7Miner Balance: &8"+String.format("%.6f", miner.getMinerBalance())));
                        player.sendMessage(ChatUtil.format("&7Location: &8"+miner.getLocation()));
                        player.sendMessage(ChatUtil.format("&7Placed: &8"+miner.isPlaced()));
                        player.sendMessage("-----------------------------------------------");
                    }
                }
            }
        }
        return true;
    }
}
