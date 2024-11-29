package net.df1015.hats.handlers;

import dev.kokiriglade.popcorn.event.AbstractEventListener;
import net.df1015.hats.HatPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public class InventoryEvent implements Listener {

    private static final int HEAD_SLOT = 39;

//    public InventoryEvent(final HatPlugin plugin) {
//        super(plugin);
//    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack helmet = Objects.requireNonNull(event.getCursor());
        if (helmet.getItemMeta() != null){
        if (helmet.getItemMeta().hasCustomModelData() && helmet.getType() == Material.FEATHER && player.getInventory().getHelmet() == helmet) {
            event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InventoryCheck(InventoryClickEvent event){
        Inventory inventory = event.getInventory();
        HumanEntity player = event.getWhoClicked();



    }
}
