package com.minecraftrpgukrainy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class RpgPlayerLevel {

    private static final String PREFIX =
            "minecraft_rpg_ukrainy_level_";

    private static final String LEVEL_KEY =
            PREFIX + "level";

    private static final String EXPERIENCE_KEY =
            PREFIX + "experience";

    private static final int START_LEVEL = 1;
    private static final int START_EXPERIENCE = 0;

    private RpgPlayerLevel() {
    }

    private static CompoundTag getData(Player player) {
        return player.getPersistentData();
    }

    public static int getLevel(Player player) {
        return Math.max(
                START_LEVEL,
                getData(player).getInt(LEVEL_KEY)
        );
    }

    public static int getExperience(Player player) {
        return Math.max(
                START_EXPERIENCE,
                getData(player).getInt(EXPERIENCE_KEY)
        );
    }

    /**
     * Досвід, необхідний для наступного рівня.
     * Рівень 1 -> 2: 8 XP.
     * Рівень 2 -> 3: 16 XP.
     * Рівень 3 -> 4: 32 XP.
     * Кожен наступний поріг подвоюється.
     */
    public static int getExperienceForNextLevel(Player player) {
        int level = getLevel(player);

        int shift = level - 1;

        if (shift >= 31) {
            return Integer.MAX_VALUE;
        }

        return 8 << shift;
    }

    public static void addExperience(Player player, int amount) {
        if (player.level().isClientSide() || amount <= 0) {
            return;
        }

        CompoundTag data = getData(player);

        long total = (long) getExperience(player) + amount;
        int level = getLevel(player);

        while (level < Integer.MAX_VALUE) {
            int shift = level - 1;

            // Захист від переповнення цілого числа.
            if (shift >= 31) {
                break;
            }

            long required = 8L << shift;

            if (total < required) {
                break;
            }

            total -= required;
            level++;

            // 5 вільних очок за кожен новий рівень.
            RpgPlayerData.addFreePoints(player, 5);
        }

        data.putInt(LEVEL_KEY, level);
        data.putInt(
                EXPERIENCE_KEY,
                (int) Math.min(total, Integer.MAX_VALUE)
        );
    }

    public static double getExperienceProgress(Player player) {
        int required = getExperienceForNextLevel(player);

        if (required <= 0 || required == Integer.MAX_VALUE) {
            return 0.0;
        }

        return Math.min(
                1.0,
                (double) getExperience(player) / required
        );
    }
}
