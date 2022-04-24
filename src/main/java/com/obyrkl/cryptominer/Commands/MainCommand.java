package com.obyrkl.cryptominer.Commands;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.ChatUtil;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length < 1){
                return true;
            }
            if(args[0].equalsIgnoreCase("give")){
                if(!player.hasPermission("obcryptominer.give")){
                    player.sendMessage(ChatUtil.format("&dCryptoMiner &8» &7Yetersiz yetki!"));
                    return true;
                }
                if(args.length < 2){
                    player.sendMessage(ChatUtil.format("&dCryptoMiner &8» &c/"+cmd.getName()+" give <player>"));
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                if(targetPlayer == null){
                    player.sendMessage(ChatUtil.format("&dCryptoMiner &8» &7Oyuncu bulunamadı!"));
                    return true;
                }
                if(targetPlayer.getInventory().firstEmpty() == -1){
                    player.sendMessage(ChatUtil.format("&dCryptoMiner &8» &7Oyuncunun çantası dolu!"));
                    return true;
                }
                UUID minerUUID = MinerUtil.createRandomUUID();
                Main.instance.getMinerManager().createMiner(targetPlayer.getUniqueId(),minerUUID);
                targetPlayer.getInventory().addItem(MinerUtil.getMinerItem(minerUUID));
                player.sendMessage(ChatUtil.format("&dCryptoMiner &8» &7Miner oyuncuya verildi!"));
            }
        }else if(sender instanceof ConsoleCommandSender){
            if(args[0].equalsIgnoreCase("give")){
                if(args.length < 2){
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                if(targetPlayer == null){
                    return true;
                }
                if(targetPlayer.getInventory().firstEmpty() == -1){
                    return true;
                }
                UUID minerUUID = MinerUtil.createRandomUUID();
                Main.instance.getMinerManager().createMiner(targetPlayer.getUniqueId(),minerUUID);
                targetPlayer.getInventory().addItem(MinerUtil.getMinerItem(minerUUID));
            }
        }
        return true;
    }
}
