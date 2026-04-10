package io.github.hello09x.devtools.core.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ExperienceUtils {

    public ExperienceUtils() {
        super();
    }

    public static int getExp(@NotNull Player player) {
        return getExpFromLevel(player.getLevel()) + Math.round((float) getExpToNext(player.getLevel()) * player.getExp());
    }

    public static int getExpFromLevel(int level) {
        return level > 30 ? (int) (4.5D * (double) level * (double) level - 162.5D * (double) level + 2220.0D) : (level > 15 ? (int) (2.5D * (double) level * (double) level - 40.5D * (double) level + 360.0D) : level * level + 6 * level);
    }

    public static double getLevelFromExp(long exp) {
        int level = getIntLevelFromExp(exp);
        float remainder = (float) exp - (float) getExpFromLevel(level);
        float progress = remainder / (float) getExpToNext(level);

        return (double) level + (double) progress;
    }

    public static int getIntLevelFromExp(long exp) {
        return exp > 1395L ? (int) ((Math.sqrt((double) (72L * exp) - 54215.0D) + 325.0D) / 18.0D) : (exp > 315L ? (int) (Math.sqrt((double) (40L * exp) - 7839.0D) / 10.0D + 8.1D) : (exp > 0L ? (int) (Math.sqrt((double) exp + 9.0D) - 3.0D) : 0));
    }

    private static int getExpToNext(int level) {
        return level >= 30 ? level * 9 - 158 : (level >= 15 ? level * 5 - 38 : level * 2 + 7);
    }

    public static void changeExp(@NotNull Player player, int exp) {
        exp += getExp(player);
        if (exp < 0) {
            exp = 0;
        }

        double levelAndExp = getLevelFromExp((long) exp);
        int level = (int) levelAndExp;

        player.setLevel(level);
        player.setExp((float) (levelAndExp - (double) level));
    }

    public static void clean(@NotNull Player player) {
        player.setLevel(0);
        player.setExp(0.0F);
    }
}
