package Main;

import org.bukkit.plugin.java.JavaPlugin;
import Listener.DeathListener;
import Listener.InteractListener;
import Listener.InventoryListener;

public class DeathPackagePlugin extends JavaPlugin {

    private static DeathPackagePlugin instance;

    public static DeathPackagePlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }
}