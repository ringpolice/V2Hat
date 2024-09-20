package net.df1015.pl;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryEvent extends Hat implements Listener {
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getSlotType().equals(InventoryType.SlotType.ARMOR) && !e.getCurrentItem().getType().equals(Material.AIR)) {
            e.setCancelled(true);
        }
    }
}
