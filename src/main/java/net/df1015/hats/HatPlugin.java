package net.df1015.hats;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.df1015.hats.commands.HatCommand;
import net.df1015.hats.handlers.ConfigHandler;
import net.df1015.hats.handlers.InventoryEvent;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HatPlugin extends JavaPlugin {

    private static @MonotonicNonNull Economy econ = null;
    private @MonotonicNonNull ConfigHandler config;
    private @MonotonicNonNull LuckPerms api;

    public static @NonNull Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryEvent(), this);

        this.config = new ConfigHandler(this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
        }

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(new HatCommand().get(this).build(), List.of("hat"));

        });
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no vault ");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public @NonNull ConfigHandler getConfigManager() {
        return config;
    }

    public @NonNull LuckPerms getApi() {
        return api;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static void explodeNearestCow(Player player) {
        Bukkit.getServer().getLogger().info("explodeNearestCow");
        ArrayList<Entity> nearestCows = (ArrayList<Entity>) player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5);
        double lowestDistanceSoFar = Double.MAX_VALUE;
        Entity closestEntity = null;

        for (Entity entity : nearestCows) {
            double distance = entity.getLocation().distance(player.getLocation());
            if (distance < lowestDistanceSoFar) {
                if (entity.getType() == EntityType.COW) {
                    lowestDistanceSoFar = distance;
                    closestEntity = entity;
                    Bukkit.getServer().getLogger().info("cow targeted");
                }else{
                    Bukkit.getServer().getLogger().info("no cows");
                }
            }
        }
        if (closestEntity != null) {
            Double randomNum = Math.random();
            if(randomNum == 0.1){
                Bukkit.getServer().getLogger().info("closestEntity: " + closestEntity.getType());
                if(closestEntity.getType() == EntityType.COW) {
                    closestEntity.getWorld().createExplosion(closestEntity.getLocation(), 5, true);
                }
            }
        }
    }
}





