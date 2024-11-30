package net.df1015.hats.menus;

import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.PaginatedPane;
import dev.kokiriglade.popcorn.inventory.pane.Pane;
import dev.kokiriglade.popcorn.inventory.pane.StaticPane;
import dev.kokiriglade.popcorn.inventory.pane.util.Slot;
import net.df1015.hats.HatPlugin;
import net.df1015.hats.handlers.ConfigHandler;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.EventListener;

public class CurrentlyOwned extends ChestGui implements EventListener {

    public CurrentlyOwned(@NonNull Component title, HumanEntity player, HatPlugin plugin) {
        super(6, title);
        final ConfigHandler config = plugin.getConfigManager();

        PaginatedPane ownedMenu = new PaginatedPane(0, 0, 9, 5);
        StaticPane clearHat = new StaticPane(4, 5, 1, 1);

        //ownedMenu.setOnClick(event -> event.setCancelled(true));

        this.addPane(ownedMenu);
        this.addPane(clearHat);
        clearHat.setPriority(Pane.Priority.HIGHEST);

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

        for (String owned : config.getDocument().getSection("hats").getRoutesAsStrings(false)) {
            String material = config.getDocument().getString("hats." + owned + ".data.item");
            Boolean debug = config.getDocument().getBoolean("debug");
            MessageBuilder display = MessageBuilder.of(plugin, config.getDocument().getString("hats." + owned + ".data.display"));
            Integer texture = config.getDocument().getInt("hats." + owned + ".data.texture");
            String permission = config.getDocument().getString("hats." + owned + ".permission");
            Double price = config.getDocument().getDouble("hats." + owned + ".price");
            MessageBuilder activate = MessageBuilder.of(plugin, config.getDocument().getString("lang.activate")).set("hat", display.component()).set("price", Math.floor(price));
            String activateSound = config.getDocument().getString("sounds.activate");

                if (player.hasPermission(permission)) {
                    ItemStack hat = new ItemStack(Material.valueOf(material)); // currently available hats
                    hatPerms.add(new GuiItem(hat, (event) -> {
                        if (event.isLeftClick() || event.isRightClick()) {
                            event.setCancelled(true);

                            ItemMeta hatMeta = hat.getItemMeta();
                            hatMeta.setCustomModelData(texture);
                            hatMeta.itemName(display.component());
                            hat.setItemMeta(hatMeta);

                            player.getInventory().setHelmet(hat);
                            player.playSound(Sound.sound().type(org.bukkit.Sound.valueOf(activateSound).key()).volume(3.0f).pitch(0.5f).build(), Sound.Emitter.self());
                            player.sendMessage(activate.component());
                            if(debug)
                            player.sendMessage("DEBUG: texture.toString(): " + texture.toString() +
                                               " - hat.getItemMeta().getCustomModelData(): " + (hat.getItemMeta() != null ? hat.getItemMeta() : "null"));

                        }
                    }));
                }
            ownedMenu.populateWithGuiItems(hatPerms);
        }


    }

}
