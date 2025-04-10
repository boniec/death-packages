package Managers;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DeathPackageManager {

    private static final Map<Integer, List<ItemStack>> packages = new HashMap<>();

    public static void savePackage(int id, List<ItemStack> items) {
        packages.put(id, new ArrayList<>(items));
    }

    public static List<ItemStack> getPackage(int id) {
        return packages.getOrDefault(id, new ArrayList<>());
    }

    public static void removePackage(int id) {
        packages.remove(id);
    }
}