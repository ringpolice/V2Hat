package net.df1015.pl.commands;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.df1015.pl.Hat;
import net.df1015.pl.handlers.GuiHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;


@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public class HatCommand implements dev.kokiriglade.popcorn.command.Command<LiteralArgumentBuilder<CommandSourceStack>, Hat> {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get(final Hat plugin) {
        return Commands.literal("hat")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("vindex.hat")
                        && commandSourceStack.getSender() instanceof Player)
                .executes(context ->{
                    FileConfiguration config = plugin.getConfig();

                    final Player player = (Player) context.getSource().getSender();
                    String open = config.getString("sounds.menu-open");

                    player.getWorld().playSound(player, Sound.valueOf(open), 3.0F, 0.5F);
                    new GuiHandler(4, Component.text("Hat Menu")).show(player);

                    return Command.SINGLE_SUCCESS;
                });
    }
}
