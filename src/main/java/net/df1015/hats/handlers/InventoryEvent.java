package net.df1015.hats.handlers;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public class InventoryEvent implements Listener {

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack helmet = Objects.requireNonNull(event.getCursor());
        if (helmet.getItemMeta() != null) {
            if (helmet.getItemMeta().hasCustomModelData() && helmet.getType() == Material.FEATHER && player.getInventory().getHelmet() == helmet) {
                event.setCancelled(true);
            }
        }
    }
}
