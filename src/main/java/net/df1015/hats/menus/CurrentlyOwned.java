package net.df1015.hats.menus;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.PagingButtons;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
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

    public CurrentlyOwned(@NonNull Component title, HumanEntity player, HatPlugin plugin) {
        super(6, ComponentHolder.of(title));
        final ConfigHandler config = plugin.getConfigManager();

        PaginatedPane ownedMenu = new PaginatedPane(0, 0, 9, 5);
        StaticPane clearHat = new StaticPane(4, 5, 1, 1, Pane.Priority.HIGH);

        PagingButtons pagingButtons = new PagingButtons(Slot.fromXY(0,0), 3, Pane.Priority.HIGHEST, ownedMenu, plugin);
        pagingButtons.setBackwardButton(new GuiItem(new ItemStack(Material.CANDLE)));
        pagingButtons.setForwardButton(new GuiItem(new ItemStack(Material.OXEYE_DAISY)));

        this.addPane(ownedMenu);
        this.addPane(clearHat);
        this.addPane(pagingButtons);

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

        ArrayList<GuiItem> hatPerms = new ArrayList<>();

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
                hatPerms.add(new GuiItem(hat, (event) -> {
                    if (event.isLeftClick() || event.isRightClick()) {
                        event.setCancelled(true);

                        player.getInventory().setHelmet(hat);
                        player.playSound(Sound.sound().type(org.bukkit.Sound.valueOf(activateSound).key()).volume(3.0f).pitch(0.5f).build(), Sound.Emitter.self());
                        player.sendMessage(activateMessage.component());
                        if(debug) player.sendMessage("DEBUG: CurrentlyOwned.java: hatPerms.add(new GuiItem(hat, (event) -> {}");
                    }
                }));
            }
            ownedMenu.populateWithGuiItems(hatPerms);
        }


    }

}
