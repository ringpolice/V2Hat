package net.df1015.pl.menus;

import dev.kokiriglade.popcorn.builder.text.MessageBuilder;
import dev.kokiriglade.popcorn.inventory.gui.GuiItem;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import dev.kokiriglade.popcorn.inventory.pane.PaginatedPane;
import net.df1015.pl.Hat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class HatShop extends ChestGui {
    Hat plugin = Hat.getPlugin(Hat.class);
    LuckPerms lp = LuckPermsProvider.get();
    Economy econ = Hat.getEconomy();

    public HatShop(int rows, @NonNull Component title, HumanEntity player) {
        super(rows, title);
        FileConfiguration config = plugin.getConfig();
        User user = lp.getUserManager().getUser(player.getUniqueId());

        PaginatedPane shopMenu = new PaginatedPane(0,0,9,5);
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for(String available : config.getConfigurationSection("hats").getKeys(false)) {
                String material = config.getString("hats."+available+".data.item");

                MessageBuilder disp = MessageBuilder.of(plugin, config.getString("hats."+available+".data.display"));

                Integer texture = config.getInt("hats."+available+".data.texture");
                String permission = config.getString("hats."+available+".permission");
                Double price = config.getDouble("hats."+available+".price");
                String error = config.getString("sounds.error");


                MessageBuilder success = MessageBuilder.of(plugin,  config.getString("lang.buy-success")).set("hat", disp.component()).set("price", Math.floor(price));
                MessageBuilder nobalance = MessageBuilder.of(plugin,  config.getString("lang.insufficient-balance")).set("hat", disp.component()).set("price", Math.floor(price));

                ItemStack hat = new ItemStack(Material.valueOf(material)); // currently available hats
                ItemMeta hatMeta = hat.getItemMeta();
                hatMeta.setCustomModelData(texture);
                hatMeta.itemName(disp.component());
                hatMeta.lore(List.of(Component.text("$"+price).color(NamedTextColor.GREEN)));

                hat.setItemMeta(hatMeta);
                if (player.hasPermission(permission)) {
                    guiItems.add(new GuiItem(hat, (event) -> {
                        if (event.isLeftClick() || event.isRightClick()) {
                            
                                if (econ.hasAccount((OfflinePlayer) player)) {
                                    if (!econ.has((OfflinePlayer) player, price)) {
                                        player.sendMessage(nobalance.component());
                                        event.getWhoClicked().getWorld().playSound(player, Sound.valueOf(error), 3.0F, 0.5F);
                                    }else{
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
