package net.df1015.hats.menus;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import net.vindexcraft.popcorn.builder.text.MessageBuilder;
import net.df1015.hats.HatPlugin;
import net.df1015.hats.handlers.ConfigHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HatShop extends ChestGui {

    LuckPerms lp = LuckPermsProvider.get();
    Economy econ = HatPlugin.getEconomy();
    private int currentPage = 0;

    public HatShop(@NonNull Component title, HumanEntity player, HatPlugin plugin) {
        super(6, ComponentHolder.of(title));
        final ConfigHandler config = plugin.getConfigManager();
        final User user = lp.getUserManager().getUser(player.getUniqueId());

        assert user != null;

        final PaginatedPane shopMenu = new PaginatedPane(0, 0, 9, 5);
        StaticPane buttons = new StaticPane(0, 5, 9, 1, Pane.Priority.HIGH);


        MessageBuilder previousPageItemName = MessageBuilder.of(plugin, config.getDocument().getString("gui.previous_page.display"));
        ItemStack previousPage = new ItemStack(Material.valueOf(config.getDocument().getString("gui.previous_page.item")));
        ItemMeta previousPageMeta = previousPage.getItemMeta();
        previousPageMeta.itemName(previousPageItemName.component());
        previousPage.setItemMeta(previousPageMeta);

        buttons.addItem(new GuiItem(previousPage, (event) -> {
            if(currentPage - 1 >= 0 && currentPage - 1 <= shopMenu.getPages() ){
                currentPage--;
                shopMenu.setPage(currentPage);
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
            if(currentPage + 1 <= shopMenu.getPages()){
                currentPage++;
                shopMenu.setPage(currentPage);
                this.update();
            }
        }), Slot.fromXY(8, 0));

        final ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (final String shopItem : config.getDocument().getSection("hats").getRoutesAsStrings(false)) {
            String shopItemMaterial = config.getDocument().getString("hats." + shopItem + ".data.item");
            MessageBuilder shopHatDisplayName = MessageBuilder.of(plugin, config.getDocument().getString("hats." + shopItem + ".data.display"));
            Boolean debug = config.getDocument().getBoolean("debug");
            Integer shopHatTexture = config.getDocument().getInt("hats." + shopItem + ".data.texture");
            String hatPermission = config.getDocument().getString("hats." + shopItem + ".permission");
            Double hatPrice = config.getDocument().getDouble("hats." + shopItem + ".price");
            String moneySymbol = config.getDocument().getString("lang.money-symbol");
            String errorSound = config.getDocument().getString("sounds.error");
            MessageBuilder hatPriceFormatted = MessageBuilder.of(plugin, moneySymbol + Math.floor(hatPrice));
            MessageBuilder successMsg = MessageBuilder.of(plugin, config.getDocument().getString("lang.buy-success")).set("_hat_", shopHatDisplayName.component()).set("_price_", hatPriceFormatted.component());
            MessageBuilder insufficientBalanceMsg = MessageBuilder.of(plugin, config.getDocument().getString("lang.insufficient-balance"))
                .set("hat", shopHatDisplayName.component())
                .set("price", Math.floor(hatPrice));

            // todo pagination
            // TODO: Need right and left arrows in the GUI for navigation
            // TODO: Need to transform the current StaticPane into a PaginatedPane.
            // TODO: In the PaginatedPane, create and put in all of the GUIs



            if (player.hasPermission(hatPermission) || player.isOp()) {
                ItemStack shopMenuItem = new ItemStack(Material.valueOf(shopItemMaterial)); // currently available hats
                ItemMeta shopMenuItemItemMeta = shopMenuItem.getItemMeta();
                shopMenuItemItemMeta.setCustomModelData(shopHatTexture);
                shopMenuItemItemMeta.itemName(shopHatDisplayName.component());
                shopMenuItemItemMeta.lore(List.of(Component.text(moneySymbol + hatPrice).color(NamedTextColor.GREEN)));
                shopMenuItem.setItemMeta(shopMenuItemItemMeta);
                guiItems.add(new GuiItem(shopMenuItem, (event) -> {
                    if (event.isLeftClick() || event.isRightClick()) {
                        event.setCancelled(true);
                        if (econ.hasAccount((OfflinePlayer) player)) {
                            if (!econ.has((OfflinePlayer) player, hatPrice)) {
                                player.sendMessage(insufficientBalanceMsg.component());
                                event.getWhoClicked()
                                    .playSound(net.kyori.adventure.sound.Sound.sound().type(org.bukkit.Sound.valueOf(errorSound).key()).volume(3.0f).pitch(0.5f).build(),
                                        net.kyori.adventure.sound.Sound.Emitter.self()
                                    );
                            } else {
                                econ.withdrawPlayer((OfflinePlayer) player, hatPrice);
                                user.data().add(Node.builder(hatPermission).build());
                                lp.getUserManager().saveUser(user);
                                player.sendMessage(successMsg.component());
                            }
                        }
                    }
                    if (debug) event.getWhoClicked().sendMessage("DEBUG: HatShop.java: options.addItem(new GuiItem(ownedMenuItem, (event) -> {}");
                }));
            }
        }
        shopMenu.populateWithGuiItems(guiItems);
        this.addPane(shopMenu);
        this.addPane(buttons);
    }

}
