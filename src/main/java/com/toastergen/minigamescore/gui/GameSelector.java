package com.toastergen.minigamescore.gui;

import com.toastergen.minigamescore.ConfigData;
import com.toastergen.minigamescore.gameinstances.GameArena;
import com.toastergen.minigamescore.gameinstances.GameState;
import com.toastergen.minigamescore.locations.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GameSelector implements Listener {
    private final WorldManager worldManager;
    private final ConfigData configData;
    private final String GUI_TITLE = "§3§lSelect Arena";

    public GameSelector(WorldManager worldManager, ConfigData configData) {
        this.worldManager = worldManager;
        this.configData = configData;
    }

    public void openGUI(Player p) {
        int size = 9 * ((worldManager.getActiveArenas().size() / 9) + 1);
        Inventory inv = Bukkit.createInventory(null, Math.min(size, 54), GUI_TITLE);

        for (GameArena arena : worldManager.getActiveArenas()) {
            ItemStack item = createIcon(arena);
            inv.addItem(item);
        }

        p.openInventory(inv);
    }

    private ItemStack createIcon(GameArena arena) {
        ItemStack item = new ItemStack(Material.FIREWORK_CHARGE);
        ItemMeta meta = item.getItemMeta();
        FireworkEffectMeta fireMeta = (FireworkEffectMeta) meta;

        String name;
        String state;
        Color color;

        switch (arena.getState()) {
            case WAITING:
                color = Color.LIME;
                state = "§aLobby";
                name = "§a§l" + arena.getMapName();
                break;

            case STARTING:
                color = Color.ORANGE;
                state = "§6Starting";
                name = "§e§l" + arena.getMapName();
                break;

            case INGAME:
                color = Color.RED;
                state = "§cInGame";
                name = "§c§l" + arena.getMapName();
                break;

            case ENDING:
                color = Color.BLACK;
                state = "§dEnding";
                name = "§d§l" + arena.getMapName();
                break;

            default:
                color = Color.GRAY;
                state = "§7Booting";
                name = "§7§lWaiting for open lobby...";
        }

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(color)
                .build();
        fireMeta.setEffect(effect);
        fireMeta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add("§8" + configData.GAME_TYPE);
        lore.add("§7");
        lore.add("§6Server: §a" + arena.getGameId());
        lore.add("§6Players: §a" + arena.getCurrentPlayers() + "/" + arena.getMaxPlayers());
        lore.add("§6State: " + state);
        lore.add("§7");
        lore.add("§eClick to join!");
        meta.setLore(lore);

//        fireMeta.setLore(lore);

        item.setItemMeta(fireMeta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();

        ItemStack clicked = event.getCurrentItem();
        if (clicked.hasItemMeta() && clicked.getItemMeta().hasLore()) {
            List<String> lore = clicked.getItemMeta().getLore();
            for (String line : lore) {
                String plainLine = ChatColor.stripColor(line);
                if (plainLine.contains("Server:")) {
                    String gameId = plainLine.replace("Server:", "").trim();
                    worldManager.joinArena(player, gameId);
                    return;
                }
            }
        }
    }
}
