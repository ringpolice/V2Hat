package net.df1015.pl.handlers;

import dev.kokiriglade.popcorn.config.AbstractConfigurationManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConfigHandler extends AbstractConfigurationManager {

    public ConfigHandler(@NotNull Plugin plugin) {
        super(plugin, "config.yml");
        reload();
    }
    @Override
    public void reload() {
        super.reload();
//        System.out.println("HatConfig reloaded");
    }
}
