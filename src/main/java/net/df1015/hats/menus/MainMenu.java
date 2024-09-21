package net.df1015.hats.menus;

import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.StaticPane;
import dev.kokiriglade.popcorn.inventory.pane.util.Slot;
import net.df1015.hats.HatPlugin;
import net.df1015.hats.handlers.ConfigHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MainMenu extends ChestGui {

    public MainMenu(int rows, @NonNull Component title) {
        super(rows, title);
        HatPlugin plugin = HatPlugin.getPlugin(HatPlugin.class);
        final ConfigHandler config = plugin.getConfigManager();
        final StaticPane menuPane = new StaticPane(0, 0, 9, 1);

        String ownedHatMenuItem = config.getDocument().getString("gui.owned.item");
        MessageBuilder ownedHatMenuDisplay = MessageBuilder.of(plugin, config.getDocument().getString("gui.owned.display"));
        MessageBuilder ownedHatMenuTitle = MessageBuilder.of(plugin, config.getDocument().getString("gui.shop.title"));


        String shopHatMenuItem = config.getDocument().getString("gui.shop.item");
        MessageBuilder shopHatMenuDisplay = MessageBuilder.of(plugin, config.getDocument().getString("gui.shop.display"));
        MessageBuilder shopHatMenuTitle = MessageBuilder.of(plugin, config.getDocument().getString("gui.shop.title"));


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

        menuPane.addItem(new GuiItem(hat, (event) -> {
            if (event.isLeftClick() || event.isRightClick()) {
                event.setCancelled(true);
                new CurrentlyOwned(ownedHatMenuTitle.component(), event.getWhoClicked(), plugin).show(event.getWhoClicked());
            }
        }), Slot.fromXY(3, 0));

        // todo clear hat button here

        menuPane.addItem(new GuiItem(shopItem, (event) -> {
            if (event.isLeftClick() || event.isRightClick()) {
                event.setCancelled(true);
                new HatShop(shopHatMenuTitle.component(), event.getWhoClicked(), plugin).show(event.getWhoClicked());
            }
        }), Slot.fromXY(5, 0));

        this.addPane(menuPane);
    }

}
