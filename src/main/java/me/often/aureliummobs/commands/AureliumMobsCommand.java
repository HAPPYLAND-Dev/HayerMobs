package me.often.aureliummobs.commands;

import me.often.aureliummobs.Main;
import me.often.aureliummobs.entities.AureliumMob;
import me.often.aureliummobs.util.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class AureliumMobsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return false;
        if (command.getName().equalsIgnoreCase("aureliummobs")) {
            if (args.length == 0) {
                //Send info
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!(sender instanceof Player player)) {
                        Main.getInstance().reloadConfig();
                        Main.getInstance().onDisable();
                        Main.getInstance().onEnable();
                        MessageUtils.sendConsoleMessage(sender, Main.getInstance().getConfigString("messages.plugin-reloaded"));
                    } else {
                        if (!player.hasPermission("aureliummobs.reload")) {
                            MessageUtils.sendMessage(Main.getInstance().getConfigString("messages.no-permission"), player);
                        } else {
                            Main.getInstance().reloadConfig();
                            Main.getInstance().onDisable();
                            Main.getInstance().onEnable();
                            MessageUtils.sendMessage(Main.getInstance().getConfigString("messages.plugin-reloaded"), player);
                        }
                    }
                    return true;
                } else {
                    try {
                        if (sender instanceof Player p) {
                            int level = Integer.parseInt(args[0]);
                            Entity monster = p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
                            new AureliumMob((Monster) monster, level, Main.getInstance());
                        }
                    } catch (Exception e) {
                        sender.sendMessage(e.getMessage());
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
