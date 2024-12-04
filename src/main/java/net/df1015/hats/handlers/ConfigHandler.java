package net.df1015.hats.handlers;

import dev.kokiriglade.popcorn.config.AbstractConfigurationManager;
import net.df1015.hats.HatPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ConfigHandler extends AbstractConfigurationManager<HatPlugin> {
    public ConfigHandler(final HatPlugin plugin) {
        super(plugin, "config.yml");
        reload();
    }

}
