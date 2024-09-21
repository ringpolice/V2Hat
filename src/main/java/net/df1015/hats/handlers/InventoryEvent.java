package net.df1015.hats.handlers;

import dev.kokiriglade.popcorn.event.AbstractEventListener;
import net.df1015.hats.HatPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class InventoryEvent extends AbstractEventListener<HatPlugin> {

    private static final int HEAD_SLOT = 39;

    public InventoryEvent(final HatPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryInteract(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getSlot() == HEAD_SLOT && !event.getCurrentItem().getType().equals(Material.AIR)) {
            event.setCancelled(true);
        }
    }

}
