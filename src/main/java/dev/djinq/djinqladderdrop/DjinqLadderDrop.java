package dev.djinq.djinqladderdrop;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class DjinqLadderDrop extends JavaPlugin {
    private MessageManager messages;
    private boolean enabled;

    @Override
    public void onEnable() {
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

    public Boolean toggle() {
        try {
            File file = new File(getDataFolder(), "config.yml");
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            boolean nextEnabled = !enabled;
            config.set("enabled", nextEnabled);
            config.save(file);
            enabled = nextEnabled;
            return enabled;
        } catch (IOException | InvalidConfigurationException exception) {
            getLogger().severe("Could not save ladder drop setting: " + exception.getMessage());
            return null;
        }
    }

    public boolean reloadPlugin() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(getDataFolder(), "config.yml"));
            boolean nextEnabled = config.getBoolean("enabled", true);
            if (!messages.reload()) {
                return false;
            }

            enabled = nextEnabled;
            return true;
        } catch (IOException | InvalidConfigurationException | RuntimeException exception) {
            getLogger().severe("Could not reload djinqLadderDrop configuration: " + exception.getMessage());
            return false;
        }
    }

    public boolean isLadderDropEnabled() {
        return enabled;
    }

    public MessageManager messages() {
        return messages;
    }
}
