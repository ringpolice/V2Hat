package net.df1015.hats.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.kokiriglade.popcorn.inventory.gui.type.ChestGui;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.df1015.hats.HatPlugin;
import net.df1015.hats.handlers.ConfigHandler;
import net.df1015.hats.menus.MainMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public final class HatCommand implements dev.kokiriglade.popcorn.command.Command<LiteralArgumentBuilder<CommandSourceStack>, HatPlugin> {

    // initial GUI for the /hat command

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get(final HatPlugin plugin) {
        return Commands.literal("hat")
            .requires(commandSourceStack -> (commandSourceStack.getSender().isOp() || commandSourceStack.getSender()
                .hasPermission("vindex.hat")) && commandSourceStack.getSender() instanceof Player)
            .executes(context -> {
                final ConfigHandler config = plugin.getConfigManager();

                final Player player = (Player) context.getSource().getSender();
                String open = config.getDocument().getString("sounds.menu-open");

                player.playSound(net.kyori.adventure.sound.Sound.sound().type(org.bukkit.Sound.valueOf(open).key()).volume(3.0f).pitch(0.5f).build(),
                    net.kyori.adventure.sound.Sound.Emitter.self()
                );
                //opens the GUI
                //new MainMenu(1, Component.text("Hat Menu")).show(player);
                ChestGui Menu = new MainMenu(1,Component.text("Hat Menu"));
                Menu.setOnGlobalClick(event -> event.setCancelled(true));
                Menu.show(player);

                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("hat.admin")).executes(context -> {
                context.getSource().getSender().sendMessage("reloaded config");

                plugin.getConfigManager().reload();

                return Command.SINGLE_SUCCESS;
            }));
    }

}
