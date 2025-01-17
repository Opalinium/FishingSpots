package me.inotsleep.fishingspots.spots;

import me.inotsleep.fishingspots.FishingSpots;
import me.inotsleep.fishingspots.utils.Utils;
import org.bukkit.Location;

public class Spot {
    private final Location location;
    private final SpotEffect effect;
    private final String rarity;
    private boolean locked = false;
    private int tick = 0;

    public Spot(String rarity, Location location) {
        this.rarity = rarity;
        this.location = location;
        this.effect = SpotsEffectConfig.effects.get(rarity);
        // No further handling required; this Spot can exist with a null effect.
    }

    public Spot(Location location) {
        String generatedRarity = Utils.generateRarity(FishingSpots.config.rarities);
        this.rarity = generatedRarity;
        this.effect = SpotsEffectConfig.effects.get(generatedRarity);
        this.location = location;
        // Null values for rarity or effect are acceptable here for game initiation logic.
    }

    public void draw() {
        if (effect != null) {
            effect.draw(tick, location);
        }
        tick++;
    }

    public boolean checkLocation(Location location) {
        return location.getWorld() == this.location.getWorld()
                && (effect == null || effect.radius >= location.distance(this.location))
                && !locked;
    }

    public SpotEffect getEffect() {
        return effect;
    }

    public String getRarity() {
        return rarity;
    }

    public void lock() {
        locked = true;
    }
}