package net.df1015.pl;

import dev.kokiriglade.popcorn.config.AbstractConfigurationManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class HatConfig extends AbstractConfigurationManager {

    public HatConfig(@NotNull Plugin plugin) {
        super(plugin, "config.yml");
        reload();
    }
    @Override
    public void reload() {
        super.reload();
//        System.out.println("HatConfig reloaded");
    }
}
