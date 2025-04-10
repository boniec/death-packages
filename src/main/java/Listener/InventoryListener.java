package Listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains(ChatColor.AQUA + "Death Package's Inventory")) {
            event.setCancelled(true);
        }
    }
}