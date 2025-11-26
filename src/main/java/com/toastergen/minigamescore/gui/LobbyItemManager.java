package com.toastergen.minigamescore.gui;

import com.toastergen.minigamescore.ConfigData;
import com.toastergen.minigamescore.locations.WorldManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyItemManager implements Listener {

    private final ConfigData configData;
    private final GameSelector gameSelector;
    private final WorldManager worldManager;

    public LobbyItemManager(ConfigData configData, GameSelector gameSelector, WorldManager worldManager) {
        this.configData = configData;
        this.gameSelector = gameSelector;
        this.worldManager = worldManager;
    }

    public void giveSelectorItem(Player player) {
        ItemStack item = new ItemStack(configData.SELECTOR_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(configData.SELECTOR_NAME);

        item.setItemMeta(meta);

        player.getInventory().setItem(configData.SELECTOR_SLOT, item);
    }

    public void giveLeaveItem(Player player) {
        ItemStack item = new ItemStack(configData.LEAVE_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(configData.LEAVE_NAME);

        item.setItemMeta(meta);

        player.getInventory().setItem(configData.LEAVE_SLOT, item);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().clear();
        giveSelectorItem(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (isSelectorItem(item)) {
                gameSelector.openGUI(player);
                event.setCancelled(true);
            }
            if (isLeaveItem(item)) {
                worldManager.leaveGame(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && (isSelectorItem(event.getCurrentItem()) || isLeaveItem(event.getCurrentItem()))) {
            event.setCancelled(true);
        }

        if (event.getHotbarButton() != -1) {
            ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (isSelectorItem(item) || isLeaveItem(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isSelectorItem(event.getItemDrop().getItemStack())
                || isLeaveItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    private boolean isSelectorItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (item.getType() != configData.SELECTOR_MATERIAL) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;

        return item.getItemMeta().getDisplayName().equals(configData.SELECTOR_NAME);
    }

    private boolean isLeaveItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (item.getType() != configData.LEAVE_MATERIAL) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;

        return item.getItemMeta().getDisplayName().equals(configData.LEAVE_NAME);
    }
}