package dev.djinq.djinqladderdrop;

import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class LadderDropCommand implements CommandExecutor, TabCompleter {
    private final DjinqLadderDrop plugin;

    public LadderDropCommand(DjinqLadderDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("djinqladderdrop.reload")) {
                plugin.messages().send(sender, "no-permission");
                return true;
            }

            plugin.messages().send(sender, plugin.reloadPlugin() ? "reload-success" : "reload-failed");
            return true;
        }
        if (args.length > 0) {
            plugin.messages().send(sender, "usage");
            return true;
        }
        if (!sender.hasPermission("djinqladderdrop.toggle")) {
            plugin.messages().send(sender, "no-permission");
            return true;
        }

        Boolean enabled = plugin.toggle();
        if (enabled == null) {
            plugin.messages().send(sender, "toggle-failed");
            return true;
        }
        plugin.messages().send(sender, enabled ? "enabled" : "disabled");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("djinqladderdrop.reload")
                && "reload".startsWith(args[0].toLowerCase(Locale.ROOT))) {
            return List.of("reload");
        }
        return List.of();
    }
}
