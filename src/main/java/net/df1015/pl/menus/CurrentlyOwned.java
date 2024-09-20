package net.df1015.pl.menus;

import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.PaginatedPane;
import dev.kokiriglade.popcorn.inventory.pane.Pane;
import dev.kokiriglade.popcorn.inventory.pane.StaticPane;
import net.df1015.pl.Hat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class CurrentlyOwned extends ChestGui implements EventListener{

    public CurrentlyOwned(int rows, @NonNull Component title, HumanEntity player) {
        super(rows, title);
        Hat plugin = Hat.getPlugin(Hat.class);
        FileConfiguration config = plugin.getConfig();

        PaginatedPane ownedMenu = new PaginatedPane(0,0,9,5);
        StaticPane  clearHat = new StaticPane(0,0,9,5);
        clearHat.setPriority(Pane.Priority.HIGHEST);

        String clearItem = config.getString("gui.clear.item");
        MessageBuilder clearItemDisplay = MessageBuilder.of(plugin,  config.getString("gui.clear.display"));
        MessageBuilder clear = MessageBuilder.of(plugin,  config.getString("lang.clear-hat"));
        MessageBuilder noClear = MessageBuilder.of(plugin,  config.getString("lang.no-hat"));

        ItemStack clearHats = new ItemStack(Material.valueOf(clearItem)); // currently available hats
        ItemMeta clearMeta = clearHats.getItemMeta();
        clearMeta.itemName(clearItemDisplay.component());
        clearHats.setItemMeta(clearMeta);

        clearHat.addItem(new GuiItem(clearHats, (event) -> {
            if (event.isLeftClick() || event.isRightClick()) {
                event.setCancelled(true);
                if(player.getInventory().getHelmet() != null) {
                    player.getInventory().setHelmet(new ItemStack((Material.AIR)));
                    player.sendMessage(clear.component());
                }else{
                    player.sendMessage(noClear.component());
                }
            }
        }
        ), 4, 2);

        ArrayList<GuiItem> hatPerms = new ArrayList<>();



        for(String owned : config.getConfigurationSection("hats").getKeys(false)) {
            String material = config.getString("hats."+owned+".data.item");
            MessageBuilder disp = MessageBuilder.of(plugin, config.getString("hats."+owned+".data.display"));
            Integer texture = config.getInt("hats."+owned+".data.texture");
            String permission = config.getString("hats."+owned+".permission");
            Double price = config.getDouble("hats."+owned+".price");
            MessageBuilder activate = MessageBuilder.of(plugin,  config.getString("lang.activate")).set("hat", disp.component()).set("price", Math.floor(price));
            String activateSound = config.getString("sounds.activate");

            if (player.hasPermission(permission)){
                ItemStack hat = new ItemStack(Material.valueOf(material)); // currently available hats
                ItemMeta hatMeta = hat.getItemMeta();
                hatMeta.setCustomModelData(texture);
                hatMeta.itemName(disp.component());
                hat.setItemMeta(hatMeta);


                hatPerms.add(new GuiItem(hat, (event) -> {
                    if(event.isLeftClick() || event.isRightClick()) {
                        event.setCancelled(true);
                        event.getWhoClicked().getInventory().setHelmet(hat);
                        event.getWhoClicked().getWorld().playSound(player, Sound.valueOf(activateSound), 3.0F, 0.5F);
                        player.sendMessage(activate.component());
                    }
                }));
            }
            ownedMenu.populateWithGuiItems(hatPerms);
        }
        this.addPane(ownedMenu);
        this.addPane(clearHat);


    }

}
