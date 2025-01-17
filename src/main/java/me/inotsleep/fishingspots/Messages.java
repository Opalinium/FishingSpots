package me.inotsleep.fishingspots;

import me.inotsleep.utils.config.AbstractConfig;
import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.config.Path;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Messages extends AbstractConfig {
    public static Map<Double, String> damageColors;
    public Messages(AbstractPlugin plugin) {
        super(plugin, "messages.yml");
    }

    @Path(value="progress.char.background")
    public static String charBackground = "&8█";
    @Path(value="progress.char.main")
    public static String charMain = "█";
    @Path(value="progress.char.current")
    public static String charCurrent = "&b❮";
    @Path(value="progress.length")
    public static int length = 20;
    @Path(value="progress.format")
    public static String format = "&a☑&r {0} &4☒";
    @Path(value = "progress.hookDamageColors")
    public static List<String> rawColors = Arrays.asList("0.0|&7", "0.25|&e", "0.5|&6", "0.75|&4");

    @Path(value="game.start")
    public static String start = "Something on a hook...";
    @Path(value="game.loose.line")
    public static String looseLine = "Crap! The fishing line broke.";
    @Path(value="game.loose.loose")
    public static String looseLoose = "The fish escaped :(";
    @Path(value="game.loose.noFishingRod")
    public static String looseNoFishingRod = "You put down your fishing rod and let go of the fish.";
    @Path(value="game.win")
    public static String win = "You catch the fish!";

    @Path(value="messages.noPermission")
    public static String noPermission = "&4&lYou don't have permission to perform this command.";


    @Override
    public String getHeader() {
        return "progress - progress bar, that will show at game in actionbar\n" +
                "    char - char settings" +
                "        background - background char (will not change color when line stressed)\n" +
                "        main - main char (will change color when line stressed))\n" +
                "        current - will show progress\n" +
                "    length - length of progress bar\n" +
                "    format - format of progress bar, where {0} is progress bar (not included in length)\n" +
                "    hookDamageColors - colors, based on line damage (double|color)\n" +
                "game - messages of game (actionbar)\n" +
                "    start - message when game started\n" +
                "    loose - messages when player looses fish\n" +
                "        line - message when line is broken\n" +
                "        loose - message when fish escaped\n" +
                "        noFishingRod - message when player switched item and bobber do not exists\n" +
                "    win - message when player catches fish\n" +
                "messages - regular chat messages from commands\n";
    }

    @Override
    public void saveDefaults() {

    }

    public static String getColor(double damage) {
        AtomicReference<Double> min = new AtomicReference<>();
        AtomicReference<Double> key = new AtomicReference<>(0d);
        damageColors.keySet().forEach(d -> {
            min.set(min.get() == null ? Math.abs(d-damage) : Math.min(min.get(), Math.abs(d-damage)));
            key.set(min.get() == Math.abs(d-damage) ? d : key.get());
        });
        return damageColors.get(key.get());
    }
}
