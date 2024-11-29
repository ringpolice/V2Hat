package net.df1015.hats.handlers;

import dev.kokiriglade.popcorn.event.AbstractEventListener;
import net.df1015.hats.HatPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class InventoryEvent implements Listener {

    private static final int HEAD_SLOT = 39;

//    public InventoryEvent(final HatPlugin plugin) {
//        super(plugin);
//    }

    @EventHandler
    public void onInventoryInteract(final InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Boolean meta = event.getCurrentItem().hasItemMeta();

        if (event.getCurrentItem() != null) {
            if (meta) {
                if (event.getCurrentItem().getItemMeta().hasCustomModelData()) {
                    if (event.getWhoClicked().getInventory().getHelmet().getItemMeta().hasCustomModelData()) {
                        event.setCancelled(true);
                    }else{
                        player.sendMessage("helmet slot has no customa data");
                    }
                }
            }else{
                player.sendMessage("meta false lol");
            }
        }else{
            player.sendMessage("event.getCurrentItem() == null");
        }
    }

//    @EventHandler
//    public void onInventoryDrag(final InventoryDragEvent event) {
//        if (event.getCursor().isEmpty()) {
//            return;
//        }
//        if(event.getCursor().getItemMeta() != null){
//            if(event.getCursor().getItemMeta().hasCustomModelData()){
//                if (event.getCursor().getType().equals(Material.FEATHER)) {
//                    event.getWhoClicked().sendMessage("2:go fuck urself");
//                    event.setCancelled(true);
//                }
//            }else{
//                event.getWhoClicked().sendMessage("no cus data");
//            }
//        }else{
//            event.getWhoClicked().sendMessage("no item meta");
//        }
//    }

}
