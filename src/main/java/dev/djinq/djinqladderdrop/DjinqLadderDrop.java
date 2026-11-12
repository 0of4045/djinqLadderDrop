package dev.djinq.djinqladderdrop;

import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DjinqLadderDrop extends JavaPlugin {
    private MessageManager messages;
    private boolean enabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ResourceMigration.mergeMissingDefaults(this, "config.yml");
        ResourceMigration.mergeMissingDefaults(this, "messages.yml");
        messages = new MessageManager(this);
        if (!reloadPlugin()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LadderDropCommand command = new LadderDropCommand(this);
        PluginCommand ladderDrop = Objects.requireNonNull(getCommand("ladderdrop"));
        ladderDrop.setExecutor(command);
        ladderDrop.setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new LadderListener(this), this);

        getServer().getConsoleSender().sendMessage(TextRenderer.render(
                "<#B3E342>djinqLadderDrop <#CACACA>made by <#B3E342>djinq "
                        + "<#CACACA>| <#B3E342>djinq.dev <#CACACA>| <#B3E342>Loaded"));
    }

    public boolean isLadderDropEnabled() {
        return enabled;
    }

    public boolean toggle() {
        enabled = !enabled;
        getConfig().set("enabled", enabled);
        saveConfig();
        return enabled;
    }

    public boolean reloadPlugin() {
        try {
            reloadConfig();
            boolean nextEnabled = getConfig().getBoolean("enabled", true);
            if (!messages.reload()) {
                return false;
            }

            enabled = nextEnabled;
            return true;
        } catch (RuntimeException exception) {
            getLogger().severe("Could not reload djinqLadderDrop configuration: " + exception.getMessage());
            return false;
        }
    }

    public MessageManager messages() {
        return messages;
    }
}
