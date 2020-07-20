package me.kuraky.antihealthbars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.hasPermission("antihealthbars.reload")) {
            AntiHealthbars plugin = AntiHealthbars.getInstance();

            plugin.reloadConfig();

            double customHealth = plugin.getConfig().getDouble("custom-health");
            plugin.setCustomHealth(customHealth);

            commandSender.sendMessage("§aReloaded, set health to §e" + customHealth);
        }
        return false;
    }
}
