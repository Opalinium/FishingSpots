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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Game ongoingGame = GameManager.getGame(player);
        State state = event.getState();

        if (ongoingGame != null) {
            switch (state) {
                case REEL_IN:
                    event.setCancelled(true);
                    ongoingGame.click();
                    break;

                case CAUGHT_FISH:
                    if (event.getCaught() instanceof Item) {
                        event.getCaught().remove();
                    }
                    event.setCancelled(true);
                    break;

                case BITE:
                default:
                    event.setCancelled(true);
                    break;
            }
            return;
        }

        if (state != State.CAUGHT_FISH) {
            return;
        }


        if (event.isCancelled()) {
            event.setCancelled(false);
        }

        Entity caughtEntity = event.getCaught();
        if (!(caughtEntity instanceof Item)) {
            return;
        }

        Item caughtItem = (Item) caughtEntity;
        ItemStack ephemeralItem = caughtItem.getItemStack().clone();
        caughtItem.remove();

        event.setCancelled(true);

        Location hookLocation = event.getHook().getLocation();
        Spot spot = SpotsManager.findSpot(hookLocation);
        if (spot != null) {
            spot.lock();
        }


        GameManager.addGame(new Game(player, spot, event.getHook(), ephemeralItem));
    }
}