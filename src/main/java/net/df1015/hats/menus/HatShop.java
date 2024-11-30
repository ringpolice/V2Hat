package net.df1015.hats.menus;

import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.PaginatedPane;
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

    public HatShop(@NonNull Component title, HumanEntity player, HatPlugin plugin) {
        super(6, title);
        final ConfigHandler config = plugin.getConfigManager();
        final User user = lp.getUserManager().getUser(player.getUniqueId());

        assert user != null;

        final PaginatedPane shopMenu = new PaginatedPane(0, 0, 9, 5);
        final ArrayList<GuiItem> guiItems = new ArrayList<>();


        //shopMenu.setOnClick(event -> event.setCancelled(true));

        for (final String available : config.getDocument().getSection("hats").getRoutesAsStrings(false)) {
            String material = config.getDocument().getString("hats." + available + ".data.item");

            MessageBuilder display = MessageBuilder.of(plugin, config.getDocument().getString("hats." + available + ".data.display"));

            Integer texture = config.getDocument().getInt("hats." + available + ".data.texture");
            String permission = config.getDocument().getString("hats." + available + ".permission");
            Double price = config.getDocument().getDouble("hats." + available + ".price");
            String error = config.getDocument().getString("sounds.error");


            MessageBuilder success = MessageBuilder.of(plugin, config.getDocument().getString("lang.buy-success")).set("hat", display.component()).set("price", Math.floor(price));
            MessageBuilder noBalance = MessageBuilder.of(plugin, config.getDocument().getString("lang.insufficient-balance"))
                .set("hat", display.component())
                .set("price", Math.floor(price));

            ItemStack hat = new ItemStack(Material.valueOf(material)); // currently available hats
            ItemMeta hatMeta = hat.getItemMeta();
            hatMeta.setCustomModelData(texture);
            hatMeta.itemName(display.component());
            hatMeta.lore(List.of(Component.text("$" + price).color(NamedTextColor.GREEN)));

            // todo pagination

            hat.setItemMeta(hatMeta);
            if (player.hasPermission(permission) || player.isOp()) {
                guiItems.add(new GuiItem(hat, (event) -> {
                    if (event.isLeftClick() || event.isRightClick()) {
                        if (econ.hasAccount((OfflinePlayer) player)) {
                            if (!econ.has((OfflinePlayer) player, price)) {
                                player.sendMessage(noBalance.component());
                                event.getWhoClicked()
                                    .playSound(net.kyori.adventure.sound.Sound.sound().type(org.bukkit.Sound.valueOf(error).key()).volume(3.0f).pitch(0.5f).build(),
                                        net.kyori.adventure.sound.Sound.Emitter.self()
                                    );
                            } else {
                                econ.withdrawPlayer((OfflinePlayer) player, price);
                                user.data().add(Node.builder(permission).build());
                                lp.getUserManager().saveUser(user);
                                player.sendMessage(success.component());
                            }
                            event.setCancelled(true);
                        }
                    }
                }));
            }
        }
        shopMenu.populateWithGuiItems(guiItems);
        this.addPane(shopMenu);
    }

}
