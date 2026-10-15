
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
     * Досвід для наступного рівня:
     * рівень 1 -> 2: 8 XP
     * рівень 2 -> 3: 16 XP
     * рівень 3 -> 4: 32 XP
     *
     * Поріг подвоюється на кожному наступному рівні.
     */
    public static int getExperienceForNextLevel(Player player) {
        return getRequiredExperience(getLevel(player));
    }

    /**
     * Безпечне обчислення порога досвіду.
     */
    public static int getRequiredExperience(int level) {
        int safeLevel = Math.max(START_LEVEL, level);
        int shift = safeLevel - 1;

        // 8 << 28 — останній поріг,
        // який можна подати додатним int.
        if (shift >= 28) {
            return Integer.MAX_VALUE;
        }

        return 8 << shift;
    }

    /**
     * Додає досвід лише на сервері.
     * Підтримує отримання кількох рівнів за раз.
     */
    public static void addExperience(Player player, int amount) {
        if (player == null
                || player.level().isClientSide()
                || amount <= 0) {
            return;
        }

        CompoundTag data = getData(player);

        long totalExperience =
                (long) getExperience(player) + amount;

        int level = getLevel(player);

        while (level < Integer.MAX_VALUE) {
            int required = getRequiredExperience(level);

            // Не дозволяємо переповненню
            // або граничному значенню зламати цикл.
            if (required == Integer.MAX_VALUE) {
                break;
            }

            if (totalExperience < required) {
                break;
            }

            totalExperience -= required;

            if (level == Integer.MAX_VALUE) {
                break;
            }

            level++;

            // 5 вільних очок за кожен новий рівень.
            RpgPlayerData.addFreePoints(player, 5);
        }

        data.putInt(LEVEL_KEY, level);
        data.putInt(
                EXPERIENCE_KEY,
                (int) Math.min(
                        Math.max(0L, totalExperience),
                        Integer.MAX_VALUE
                )
        );
    }

    /**
     * Прогрес до наступного рівня у діапазоні 0.0–1.0.
     */
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

