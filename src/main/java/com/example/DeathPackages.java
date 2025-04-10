package com.example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeathPackages extends JavaPlugin implements Listener {
    private File configFile;
    private FileConfiguration config;
    private Map<UUID, List<ItemStack>> lootChests = new HashMap<>();
    private Set<UUID> lootChestsEnabled = new HashSet<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer != null && lootChestsEnabled.contains(killer.getUniqueId())) {
            UUID uniqueID = UUID.randomUUID();
            List<ItemStack> items = new ArrayList<>(event.getDrops());
            lootChests.put(uniqueID, items);
            event.getDrops().clear();

            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            ItemStack chest = new ItemStack(Material.CHEST);
            ItemMeta meta = chest.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Chest-name", "&6Loot Chest")));

            List<String> lore = new ArrayList<>();
            for (String line : config.getStringList("Chest-lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("{killer}", killer.getName())
                        .replace("{victim}", victim.getName())
                        .replace("{date}", date)));
            }
            lore.add("ID: " + uniqueID.toString());
            meta.setLore(lore);
            chest.setItemMeta(meta);
            victim.getWorld().dropItemNaturally(victim.getLocation(), chest);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.CHEST && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<String> lore = item.getItemMeta().getLore();
                if (lore != null && !lore.isEmpty()) {
                    String idString = lore.get(lore.size() - 1).replace("ID: ", "");
                    UUID chestID = UUID.fromString(idString);
                    Player player = event.getPlayer();

                    if (lootChests.containsKey(chestID)) {
                        if (player.isSneaking()) {
                            Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Stashes Items");
                            for (ItemStack loot : lootChests.get(chestID)) {
                                inventory.addItem(loot);
                            }
                            player.openInventory(inventory);
                        } else {
                            for (ItemStack loot : lootChests.get(chestID)) {
                                player.getInventory().addItem(loot);
                            }
                            lootChests.remove(chestID);
                            player.getInventory().removeItem(item);
                            player.sendMessage(ChatColor.GREEN + "You have collected the items from the chest.");
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "No items found in this chest!");
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Stashes Items")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Stashes Items")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Stashes Items")) {
            // Cleanup if necessary
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("stashes")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }
            Player player = (Player) sender;

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("on")) {
                    lootChestsEnabled.add(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Stashes have been enabled!");
                } else if (args[0].equalsIgnoreCase("off")) {
                    lootChestsEnabled.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Stashes have been disabled!");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Usage: /stashes <on|off>");
                }
            } else {
                player.sendMessage(ChatColor.GRAY + "Usage: /stashes <on|off>");
            }
            return true;
        }
        return false;
    }

    public void saveConfigFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }

    public Set<UUID> getLootChestsEnabled() {
        return lootChestsEnabled;
    }
}
