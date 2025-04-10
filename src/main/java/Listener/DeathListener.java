package Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import Utils.Utils;
import Main.DeathPackagePlugin;
import Managers.DeathPackageManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DeathListener implements Listener {

    private static int deathIdCounter = 0;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        int id = ++deathIdCounter;

        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();

        String name = DeathPackagePlugin.getInstance().getConfig().getString("death_package.name", "&e&lDEATH PACKAGE");
        meta.setDisplayName(Utils.color(name));

        List<String> loreRaw = DeathPackagePlugin.getInstance().getConfig().getStringList("death_package.lore");
        List<String> lore = new ArrayList<>();

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        for (String line : loreRaw) {
            lore.add(Utils.color(line
                    .replace("%id%", String.valueOf(id))
                    .replace("%player%", victim.getName())
                    .replace("%date%", date)
            ));
        }

        meta.setLore(lore);
        chest.setItemMeta(meta);

        DeathPackageManager.savePackage(id, event.getDrops());
        victim.getWorld().dropItemNaturally(victim.getLocation(), chest);
        event.getDrops().clear();
    }
}