package com.minecraftrpgukrainy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgPlayerData {

    private static final String PREFIX =
            "minecraft_rpg_ukrainy_";

    private static final String FREE_POINTS =
            PREFIX + "free_points";

    private RpgPlayerData() {
    }

    private static CompoundTag getData(Player player) {
        return player.getPersistentData();
    }

    private static String getStatKey(RpgStats.Stat stat) {
        return PREFIX + stat.name().toLowerCase(
                java.util.Locale.ROOT
        );
    }

    public static int getFreePoints(Player player) {
        return getData(player).getInt(FREE_POINTS);
    }

    public static void addFreePoints(
            Player player, int amount) {

        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Кількість очок не може бути від'ємною."
            );
        }

        CompoundTag data = getData(player);

        data.putInt(
                FREE_POINTS,
                Math.addExact(getFreePoints(player), amount)
        );
    }

    public static int getPoints(
            Player player, RpgStats.Stat stat) {

        return getData(player).getInt(getStatKey(stat));
    }

    public static boolean allocatePoint(
            Player player, RpgStats.Stat stat) {

        if (stat == null || getFreePoints(player) <= 0) {
            return false;
        }

        CompoundTag data = getData(player);

        int current = getPoints(player, stat);

        data.putInt(
                getStatKey(stat),
                Math.addExact(current, 1)
        );

        data.putInt(
                FREE_POINTS,
                getFreePoints(player) - 1
        );

        return true;
    }

    public static boolean removePoint(
            Player player, RpgStats.Stat stat) {

        if (stat == null || getPoints(player, stat) <= 0) {
            return false;
        }

        CompoundTag data = getData(player);

        data.putInt(
                getStatKey(stat),
                getPoints(player, stat) - 1
        );

        data.putInt(
                FREE_POINTS,
                Math.addExact(getFreePoints(player), 1)
        );

        return true;
    }

    public static int getTotalAllocatedPoints(Player player) {
        int total = 0;

        for (RpgStats.Stat stat : RpgStats.Stat.values()) {
            total = Math.addExact(
                    total,
                    getPoints(player, stat)
            );
        }

        return total;
    }

    public static boolean isSkillUnlocked(
            Player player, RpgStats.Skill skill) {

        return skill != null
                && getPoints(
                        player, skill.getRequiredStat()
                ) >= RpgStats.SKILL_UNLOCK_POINTS;
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player replacement = event.getEntity();

        CompoundTag oldData = original.getPersistentData();
        CompoundTag newData = replacement.getPersistentData();

        newData.putInt(
                FREE_POINTS,
                oldData.getInt(FREE_POINTS)
        );

        for (RpgStats.Stat stat : RpgStats.Stat.values()) {
            String key = getStatKey(stat);

            newData.putInt(key, oldData.getInt(key));
        }
    }
}
