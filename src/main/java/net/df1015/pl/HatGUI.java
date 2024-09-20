package net.df1015.pl;

import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.PaginatedPane;
import net.df1015.pl.menus.CurrentlyOwned;
import net.df1015.pl.menus.HatShop;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class HatGUI extends ChestGui {

    public HatGUI(int rows, @NonNull Component title) {
        super(rows, title);
        Hat plugin = Hat.getPlugin(Hat.class);
        FileConfiguration config = plugin.getConfig();
        PaginatedPane shopMenu = new PaginatedPane(0,0,9,5);

        String ownedHatMenuItem = config.getString("gui.owned.item");
        MessageBuilder ownedHatMenuDisplay = MessageBuilder.of(plugin,  config.getString("gui.owned.display"));


        String shopHatMenuItem = config.getString("gui.shop.item");
        MessageBuilder shopHatMenuDisplay = MessageBuilder.of(plugin,  config.getString("gui.shop.display"));

        // to-do:
        // add clear hat button


        ItemStack hat = new ItemStack(Material.valueOf(ownedHatMenuItem)); // currently owned hats
        ItemMeta hatMeta = hat.getItemMeta();
        hatMeta.setCustomModelData(1); // random int for now
        hatMeta.itemName(ownedHatMenuDisplay.component());
        hat.setItemMeta(hatMeta);

        ItemStack shopItem = new ItemStack(Material.valueOf(shopHatMenuItem)); // currently purchasable hats
        ItemMeta shopMeta = shopItem.getItemMeta();
        shopMeta.itemName(shopHatMenuDisplay.component());
        shopMeta.setCustomModelData(2); // random int for now
        shopItem.setItemMeta(shopMeta);

        shopMenu.populateWithGuiItems(Arrays.asList( // add currently owned and purchasable options
                new GuiItem(hat, (event) -> {
                    if(event.isLeftClick() || event.isRightClick()) {
                        event.setCancelled(true);
                        shopMenu.clear();
                        new CurrentlyOwned(4, Component.text("Your hats:"), event.getWhoClicked()).show(event.getWhoClicked());
                    }
                }),
                new GuiItem(shopItem, (event) -> {
                    if(event.isLeftClick() || event.isRightClick()) {
                        event.setCancelled(true);
                        shopMenu.clear();
                        new HatShop(4, Component.text("Purchasable hats:"), event.getWhoClicked()).show(event.getWhoClicked());
                    }
                })
        ));
        this.addPane(shopMenu);
    }
}
