
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

    public static int getExperienceForNextLevel(Player player) {
        int level = getLevel(player);

        long required = 100L + (long) (level - 1) * 50L;

        return (int) Math.min(required, Integer.MAX_VALUE);
    }

    public static void addExperience(Player player, int amount) {
        if (player.level().isClientSide() || amount <= 0) {
            return;
        }

        CompoundTag data = getData(player);

        long total = (long) getExperience(player) + amount;
        int level = getLevel(player);

        while (level < Integer.MAX_VALUE) {
            long required =
                    100L + (long) (level - 1) * 50L;

            if (total < required) {
                break;
            }

            total -= required;
            level++;

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

        if (required <= 0) {
            return 1.0;
        }

        return Math.min(
                1.0,
                (double) getExperience(player) / required
        );
    }
}
