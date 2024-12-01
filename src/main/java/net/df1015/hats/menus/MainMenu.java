package net.df1015.hats.menus;

import com.mojang.brigadier.Message;
import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.StaticPane;
import dev.kokiriglade.popcorn.inventory.pane.util.Slot;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.df1015.hats.HatPlugin;
import net.df1015.hats.handlers.ConfigHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MainMenu extends ChestGui {
    public MainMenu(int rows, @NonNull Component title, CommandSender p) {
        super(rows, title);
        HatPlugin plugin = HatPlugin.getPlugin(HatPlugin.class);
        final ConfigHandler config = plugin.getConfigManager();
        final StaticPane options = new StaticPane(0, 0, 9, 1);

        String ownedHatMenuItem = config.getDocument().getString("gui.owned.item");
        MessageBuilder ownedHatMenuDisplay = MessageBuilder.of(plugin, config.getDocument().getString("gui.owned.display"));
        MessageBuilder ownedHatMenuTitle = MessageBuilder.of(plugin, config.getDocument().getString("gui.owned.title"));

        String shopHatMenuItem = config.getDocument().getString("gui.shop.item");
        MessageBuilder shopHatMenuDisplay = MessageBuilder.of(plugin, config.getDocument().getString("gui.shop.display"));
        MessageBuilder shopHatMenuTitle = MessageBuilder.of(plugin, config.getDocument().getString("gui.shop.title"));

        Boolean debug = config.getDocument().getBoolean("debug");

        ItemStack ownedMenuItem = new ItemStack(Material.valueOf(ownedHatMenuItem)); // currently owned hats
        ItemMeta ownedMenuItemMeta = ownedMenuItem.getItemMeta();
        ownedMenuItemMeta.setCustomModelData(1);
        ownedMenuItemMeta.itemName(ownedHatMenuDisplay.component());
        ownedMenuItem.setItemMeta(ownedMenuItemMeta);

        ItemStack shopMenuItem = new ItemStack(Material.valueOf(shopHatMenuItem)); // currently purchasable hats
        ItemMeta shopMenuItemMeta = shopMenuItem.getItemMeta();
        shopMenuItemMeta.itemName(shopHatMenuDisplay.component());
        shopMenuItemMeta.setCustomModelData(2);
        shopMenuItem.setItemMeta(shopMenuItemMeta);

        options.addItem(new GuiItem(ownedMenuItem, (event) -> {
            if (event.isLeftClick() || event.isRightClick()) {
                event.setCancelled(true);
                ChestGui ownedHatMenu = new CurrentlyOwned(ownedHatMenuTitle.component(),event.getWhoClicked(),plugin);
                ownedHatMenu.setOnGlobalClick(event1 -> event1.setCancelled(true));
                ownedHatMenu.show(event.getWhoClicked());
                if (debug) event.getWhoClicked().sendMessage("DEBUG: MainMenu.java: options.addItem(new GuiItem(ownedMenuItem, (event) -> {}");
            }
        }), Slot.fromXY(3, 0));

        Player player = (Player) p;
        ItemStack currentHat = player.getInventory().getHelmet();
        ItemMeta currentHatMeta = currentHat.getItemMeta();
        Component preNameChange = currentHatMeta.itemName();
        currentHatMeta.itemName(Component.text(preNameChange+""));
        currentHat.setItemMeta(currentHatMeta);

        if(currentHat != null && !currentHat.getType().equals(Material.AIR)){
            options.addItem(new GuiItem(currentHat, (event) -> {
                if (event.isLeftClick() || event.isRightClick()) {
                    event.setCancelled(true);
                }
            }), Slot.fromXY(4, 0));
        }

        options.addItem(new GuiItem(shopMenuItem, (event) -> {
            if (event.isLeftClick() || event.isRightClick()) {
                event.setCancelled(true);
                ChestGui shopHatMenu = new HatShop(shopHatMenuTitle.component(), event.getWhoClicked(), plugin);
                shopHatMenu.setOnGlobalClick(event1 -> event1.setCancelled(true));
                shopHatMenu.show(event.getWhoClicked());
                if (debug) event.getWhoClicked().sendMessage("DEBUG: MainMenu.java: options.addItem(new GuiItem(shopMenuItem, (event) -> {}");
            }
        }), Slot.fromXY(5, 0));

        this.addPane(options);
    }

}
