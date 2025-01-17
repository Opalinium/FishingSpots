package me.inotsleep.fishingspots.game;

import me.inotsleep.fishingspots.FishingSpots;
import me.inotsleep.fishingspots.Messages;
import me.inotsleep.fishingspots.spots.Spot;
import me.inotsleep.fishingspots.spots.SpotEffect;
import me.inotsleep.fishingspots.spots.SpotsManager;
import me.inotsleep.fishingspots.utils.Utils;
import me.inotsleep.utils.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class Game {
    // Core references
    final Player player;
    private final Spot spot;
    private final SpotEffect effect;
    private final FishHook hook;

    // Ephemeral item caught from vanilla fishing logic
    private final ItemStack ephemeralItem;

    // Minigame tracking
    private double line = 0;
    private double catching = 0;
    private boolean end = false;

    /**
     * Constructor that includes an ephemeralItem if one was intercepted by the Listener.
     */
    public Game(Player player, Spot spot, FishHook hook, ItemStack ephemeralItem) {
        this.player = player;
        this.spot = spot;
        this.hook = hook;
        this.ephemeralItem = ephemeralItem;
        this.effect = (spot != null) ? spot.getEffect() : null;
    }

    public void tick() {
        if (end) return;

        if (!player.isOnline() || hook.isDead()) {
            stopGame(Reason.UNKNOWN);
            return;
        }

        // Adjust line and catching (progress) based on SpotEffect or default
        if (effect != null) {
            line = Math.max(line - effect.getLineRestore().generate(), 0);
            catching = Math.max(catching - effect.getCatchingRegress().generate(), -1);
        } else {
            line = Math.max(line - 0.02, 0);
            catching = Math.max(catching - 0.01, -1);
        }

        // If catching is fully regressed, the fish is lost
        if (catching <= -1) {
            stopGame(Reason.LOOSE);
        } else {
            print();
        }
    }

    public void click() {
        if (end) return;

        // Increase line/catching on click
        if (effect != null) {
            line = Math.min(line + effect.getLineDamage().generate(), 1);
            catching = Math.min(catching + effect.getCatchingProgress().generate(), 1);
        } else {
            line = Math.min(line + 0.3, 1);
            catching = Math.min(catching + 0.2, 1);
        }

        // If the line hits max, it snaps; if catching hits max, fish is caught
        if (line >= 1) {
            stopGame(Reason.LINE);
        } else if (catching >= 1) {
            stopGame(Reason.CATCH);
        }
    }

    public void stopGame(Reason reason) {
        end = true;

        switch (reason) {
            case LINE:
                // The fishing line snapped
                sendActionBarMessage(Messages.looseLine);
                damageHook();
                break;

            case CATCH:
                // The player successfully caught the fish
                sendActionBarMessage(Messages.win);

                if (spot != null) {
                    // If there's a custom spot, optionally do custom awarding logic
                    handleCustomSpotLoot();
                } else {
                    // Return the ephemeral item if we have one
                    handleEphemeralItemReward();
                }
                damageHook();
                break;

            case LOOSE:
                // The fish got away
                sendActionBarMessage(Messages.looseLoose);
                damageHook();
                break;

            case UNKNOWN:
                if (player.isOnline()) {
                    sendActionBarMessage(Messages.looseNoFishingRod);
                }
                break;
        }

        // Cleanup
        GameManager.removeGame(this);
        if (spot != null) {
            SpotsManager.removeSpot(spot);
        }
    }

    private void handleCustomSpotLoot() {
        DropItems dropItem = null;
        try {
            dropItem = FishingSpots.rewardsConfig.items.get(
                    Utils.generateRarity(FishingSpots.rewardsConfig.rarities.get(spot.getRarity()))
            );
        } catch (Exception e) {
            FishingSpots.getInstance().getLogger().severe(
                    "Invalid rewards.yml configuration. Rarities do not have 100% rarity!"
            );
            Bukkit.getPluginManager().disablePlugin(FishingSpots.getInstance());
        }

        if (dropItem != null) {
            ItemStack stack = dropItem.itemStack.clone();
            stack.setAmount((int) Utils.random(dropItem.min, dropItem.max));
            player.getInventory().addItem(stack);
        }
    }

    private void handleEphemeralItemReward() {
        if (ephemeralItem != null && ephemeralItem.getType() != Material.AIR) {
            // Give the item back to the player
            player.getInventory().addItem(ephemeralItem);
        }
    }

    private void damageHook() {
        // Increase damage on whichever fishing rod the player is using
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType() != Material.FISHING_ROD) {
            stack = player.getInventory().getItemInOffHand();
        }

        if (stack.getType() == Material.FISHING_ROD) {
            Damageable meta = (Damageable) stack.getItemMeta();
            if (meta != null) {
                meta.setDamage(meta.hasDamage() ? meta.getDamage() + 1 : 1);
                stack.setItemMeta(meta);
            }
        }

        // Remove the bobber from the world
        hook.remove();
    }

    private void sendActionBarMessage(String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }

    /**
     * Renders the "progress bar" for the fish-catching minigame in the action bar.
     */
    private void print() {
        int length = Messages.length;
        int progress = (int) (length - Math.floor(length * (catching + 1) / 2));
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (progress > 0) {
                progress--;
                str.append(progress == 0
                        ? "&r" + Messages.charCurrent
                        : Messages.getColor(line) + Messages.charMain);
            } else {
                str.append("&r").append(Messages.charBackground);
            }
        }

        if (!player.isOnline()) {
            stopGame(Reason.UNKNOWN);
            return;
        }

        // Display the bar
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                        MessageUtil.parsePlaceholders(Messages.format, str.toString()))));
    }

    public enum Reason {
        LINE, CATCH, LOOSE, UNKNOWN
    }
}