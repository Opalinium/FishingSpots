package me.inotsleep.fishingspots.utils;

import me.inotsleep.fishingspots.game.Game;
import me.inotsleep.fishingspots.game.GameManager;
import me.inotsleep.fishingspots.spots.Spot;
import me.inotsleep.fishingspots.spots.SpotsManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        // If the player is already in a minigame, just handle the existing game logic
        if (GameManager.getGame(player) != null) {
            if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH
                    || event.getState() == PlayerFishEvent.State.REEL_IN) {
                event.setCancelled(true);
                GameManager.getGame(player).click();
            } else if (event.getState() == PlayerFishEvent.State.BITE) {
                event.setCancelled(true);
            }
            return;
        }

        // Only proceed if the fish is actually caught
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        // We want to intercept the "vanilla" caught item
        Entity caughtEntity = event.getCaught();

        // Cancel so the vanilla item doesn't go into the player's inventory
        event.setCancelled(true);

        // If there's something actually caught, remove it from the world and store an ephemeral item
        ItemStack ephemeralItem = null;
        if (caughtEntity instanceof Item) {
            Item caughtItem = (Item) caughtEntity;
            ephemeralItem = caughtItem.getItemStack().clone(); // store a copy
            caughtItem.remove();                               // remove it from the world
        }

        // Lock the spot if applicable
        Location hookLocation = event.getHook().getLocation();
        Spot spot = SpotsManager.findSpot(hookLocation);
        if (spot != null) {
            spot.lock();
        }

        GameManager.addGame(new Game(player, spot, event.getHook(), ephemeralItem));
    }
}