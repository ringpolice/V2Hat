package net.df1015.hats.menus;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.PagingButtons;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import net.vindexcraft.popcorn.builder.text.MessageBuilder;
import net.df1015.hats.HatPlugin;
import net.df1015.hats.handlers.ConfigHandler;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.EventListener;

public class CurrentlyOwned extends ChestGui implements EventListener {

    private int currentPage = 0;

    public CurrentlyOwned(@NonNull Component title, HumanEntity player, HatPlugin plugin) {
        super(6, ComponentHolder.of(title));
        final ConfigHandler config = plugin.getConfigManager();

        PaginatedPane ownedMenu = new PaginatedPane(0, 0, 9, 5);
        StaticPane buttons = new StaticPane(0, 5, 9, 1, Pane.Priority.HIGH);
        StaticPane clearHat = new StaticPane(4, 5, 1, 1, Pane.Priority.HIGHEST);

        //previous page button

        MessageBuilder previousPageItemName = MessageBuilder.of(plugin, config.getDocument().getString("gui.previous_page.display"));
        ItemStack previousPage = new ItemStack(Material.valueOf(config.getDocument().getString("gui.previous_page.item")));
        ItemMeta previousPageMeta = previousPage.getItemMeta();
        previousPageMeta.itemName(previousPageItemName.component());
        previousPage.setItemMeta(previousPageMeta);

        buttons.addItem(new GuiItem(previousPage, (event) -> {
           if(currentPage - 1 >= 0 && currentPage - 1 <= ownedMenu.getPages() ){
               currentPage--;
               ownedMenu.setPage(currentPage);
               this.update();
           }
        }), Slot.fromXY(0, 0));

        //next page button

        MessageBuilder nextPageItemName = MessageBuilder.of(plugin, config.getDocument().getString("gui.next_page.display"));
        ItemStack nextPage = new ItemStack(Material.valueOf(config.getDocument().getString("gui.next_page.item")));
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.itemName(nextPageItemName.component());
        nextPage.setItemMeta(nextPageMeta);

        buttons.addItem(new GuiItem(nextPage, (event) -> {
            if(currentPage + 1 <= ownedMenu.getPages()){
                currentPage++;
                ownedMenu.setPage(currentPage);
                this.update();
            }
        }), Slot.fromXY(8, 0));

        String clearItem = config.getDocument().getString("gui.clear.item");
        MessageBuilder clearItemDisplay = MessageBuilder.of(plugin, config.getDocument().getString("gui.clear.display"));
        MessageBuilder clear = MessageBuilder.of(plugin, config.getDocument().getString("lang.clear-hat"));
        MessageBuilder noClear = MessageBuilder.of(plugin, config.getDocument().getString("lang.no-hat"));

        ItemStack clearHats = new ItemStack(Material.valueOf(clearItem));
        ItemMeta clearMeta = clearHats.getItemMeta();
        clearMeta.itemName(clearItemDisplay.component());
        clearHats.setItemMeta(clearMeta);

        clearHat.addItem(new GuiItem(clearHats, (event) -> {
            if (event.isLeftClick() || event.isRightClick()) {
                event.setCancelled(true);
                if (player.getInventory().getHelmet() != null) {
                    player.getInventory().setHelmet(new ItemStack((Material.AIR)));
                    player.sendMessage(clear.component());
                } else {
                    player.sendMessage(noClear.component());
                }
            }
        }), Slot.fromXY(0, 0));

        ArrayList<GuiItem> ownedHatsGUI = new ArrayList<>();

        // todo pagination

        for (String ownedHat : config.getDocument().getSection("hats").getRoutesAsStrings(false)) {
            String ownedHatMaterial = config.getDocument().getString("hats." + ownedHat + ".data.item");
            Boolean debug = config.getDocument().getBoolean("debug");
            MessageBuilder ownedHatDisplayName = MessageBuilder.of(plugin, config.getDocument().getString("hats." + ownedHat + ".data.display"));
            Integer ownedHatTexture = config.getDocument().getInt("hats." + ownedHat + ".data.texture");
            String hatPermission = config.getDocument().getString("hats." + ownedHat + ".permission");
            Double hatPrice = config.getDocument().getDouble("hats." + ownedHat + ".price");
            MessageBuilder moneySymbol = MessageBuilder.of(plugin, config.getDocument().getString("lang.money-symbol"));
            MessageBuilder hatPriceFormatted = MessageBuilder.of(plugin, ""+moneySymbol.component() + Math.floor(hatPrice));
            MessageBuilder activateMessage = MessageBuilder.of(plugin, config.getDocument().getString("lang.activate")).set("_hat_", ownedHatDisplayName.component()).set("_price_", hatPriceFormatted.component());
            String activateSound = config.getDocument().getString("sounds.activate");

            if (player.hasPermission(hatPermission) || player.isOp()) {
                ItemStack hat = new ItemStack(Material.valueOf(ownedHatMaterial)); // currently available hats
                ItemMeta hatMeta = hat.getItemMeta();
                hatMeta.setCustomModelData(ownedHatTexture);
                hatMeta.itemName(ownedHatDisplayName.component());
                hat.setItemMeta(hatMeta);
                ownedHatsGUI.add(new GuiItem(hat, (event) -> {
                    if (event.isLeftClick() || event.isRightClick()) {
                        event.setCancelled(true);
                        player.getInventory().setHelmet(hat);
                        player.playSound(Sound.sound().type(org.bukkit.Sound.valueOf(activateSound).key()).volume(3.0f).pitch(0.5f).build(), Sound.Emitter.self());
                        player.sendMessage(activateMessage.component());
                        if(debug) player.sendMessage("DEBUG: CurrentlyOwned.java: ownedHatsGUI.add(new GuiItem(hat, (event) -> {}");
                    }
                }));
            }
            ownedMenu.populateWithGuiItems(ownedHatsGUI);
            this.addPane(ownedMenu);
            this.addPane(buttons);
            this.addPane(clearHat);
        }


    }
}
