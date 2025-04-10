package Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import Utils.Utils;
import Main.DeathPackagePlugin;
import Managers.DeathPackageManager;

import java.util.List;

public class InteractListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.CHEST) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        String name = ChatColor.stripColor(meta.getDisplayName());
        String expectedName = ChatColor.stripColor(Utils.color(DeathPackagePlugin.getInstance().getConfig().getString("death_package.name")));

        if (!name.equalsIgnoreCase(expectedName)) return;

        List<String> lore = meta.getLore();
        String idLine = ChatColor.stripColor(
                lore.stream()
                        .filter(line -> line.toLowerCase().contains("id:"))
                        .findFirst()
                        .orElse("Id: 0")
        );

        int id = Integer.parseInt(idLine.split(":")[1].trim());

        Player player = event.getPlayer();
        event.setCancelled(true);

        if (player.isSneaking()) {
            Inventory gui = Bukkit.createInventory(null, 54, ChatColor.AQUA + "Death Package's Inventory");
            int slot = 0;
            for (ItemStack stack : DeathPackageManager.getPackage(id)) {
                if (slot >= gui.getSize()) break;
                gui.setItem(slot++, stack);
            }
            player.openInventory(gui);
        } else {
            List<ItemStack> drops = DeathPackageManager.getPackage(id);
            for (ItemStack stack : drops) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(stack);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), stack);
                }
            }
            player.getInventory().removeItem(new ItemStack(Material.CHEST, 1));
            DeathPackageManager.removePackage(id);
        }
    }
}