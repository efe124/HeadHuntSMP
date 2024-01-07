package me.efekos.headhuntsmp;

import me.efekos.headhuntsmp.classes.PlayerData;
import me.efekos.headhuntsmp.events.PickupOwnHead;
import me.efekos.headhuntsmp.events.Place;
import me.efekos.headhuntsmp.events.PlayerCraftHead;
import me.efekos.headhuntsmp.events.PlayerKilled;
import me.efekos.headhuntsmp.utils.AnchorRecipeManager;
import me.efekos.headhuntsmp.utils.Logger;
import me.efekos.headhuntsmp.utils.HeadRecipeManager;
import me.efekos.simpler.Metrics;
import me.efekos.simpler.config.ListDataManager;
import me.efekos.simpler.config.YamlConfig;
import me.efekos.simpler.items.ItemManager;
import me.efekos.simpler.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeadHuntSMP extends JavaPlugin {

    private static HeadHuntSMP plugin;
    public static YamlConfig gameConfig;
    public static ListDataManager<PlayerData> PLAYER_DATA;

    public static HeadHuntSMP getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Logger.Info("Plugin starting");

        Metrics metrics = new Metrics(this,18888);

        Logger.Log("Loading config");
        gameConfig = new YamlConfig("config.yml",this);
        gameConfig.setup();

        Logger.Log("Loading recipes");
        if(gameConfig.getBoolean("extra-head.enabled",true)){
            try {
                if(gameConfig.getBoolean("extra-head.use-default",true)) HeadRecipeManager.loadDefaultRecipe(this);
                else HeadRecipeManager.loadConfigRecipe(this);

                Bukkit.addRecipe(HeadRecipeManager.getLastLoadedRecipe());
                Bukkit.addRecipe(AnchorRecipeManager.getLastLoadedRecipe());
            } catch (Exception e){
                Logger.Error("There was an error loading the recipes:");
                Logger.Error(e.getMessage());
            }
        }

        if(gameConfig.getBoolean("unban-anchor.enabled",true)){
            try {
                if(gameConfig.getBoolean("unban-anchor.use-default",true)) AnchorRecipeManager.loadDefaultRecipe(this);
                else AnchorRecipeManager.loadConfigRecipe(this);

                Bukkit.addRecipe(AnchorRecipeManager.getLastLoadedRecipe());
            } catch (Exception e){
                Logger.Error("There was an error loading the recipes:");
                Logger.Error(e.getMessage());
            }
        }

        Logger.Log("Loading events");
        getServer().getPluginManager().registerEvents(new PickupOwnHead(),this);
        getServer().getPluginManager().registerEvents(new PlayerKilled(),this);
        getServer().getPluginManager().registerEvents(new PlayerCraftHead(),this);
        getServer().getPluginManager().registerEvents(new Place(),this);

        Logger.Log("Loading data");
        PLAYER_DATA = new ListDataManager<>("\\data\\PlayerData.json",this);
        PLAYER_DATA.load(PlayerData[].class);

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        ItemManager.setPlugin(this);
        MenuManager.setPlugin(this);

        Logger.Success("Plugin Started!");
    }

    @Override
    public void onDisable() {
        Logger.Info("Plugin Stopping");

        Logger.Log("Saving data");
        PLAYER_DATA.save();

        Logger.Success("Plugin Stopped!");
    }
}
