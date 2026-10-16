
package com.minecraftrpgukrainy;

import net.minecraft.world.entity.player.Player;

/**
 * Розрахунок бойових бонусів характеристик.
 *
 * Цей клас обчислює значення, але ще не застосовує
 * їх автоматично до бойових подій Minecraft.
 */
public final class RpgStatEffects {

    private RpgStatEffects() {
    }

    public static double getEffectivePoints(
            Player player,
            RpgStats.Stat stat
    ) {
        if (player == null || stat == null) {
            return 0.0;
        }

        int points = RpgPlayerData.getPoints(player, stat);

        if (points <= 0) {
            return 0.0;
        }

        int total = RpgPlayerData.getTotalAllocatedPoints(player);

        if (total <= 0) {
            return 0.0;
        }

        double share =
                (double) points / (double) total;

        // До 60% включно очки діють повністю.
        if (share <= 0.60) {
            return points;
        }

        // Після 60% надлишкова частина діє наполовину.
        double normalPoints = total * 0.60;
        double excessPoints = points - normalPoints;

        return normalPoints + excessPoints * 0.5;
    }

    public static double getAttackBonus(Player player) {
        return getEffectivePoints(
                    player, RpgStats.Stat.STRENGTH
                ) * 0.5
                + getEffectivePoints(
                    player, RpgStats.Stat.PERCEPTION
                ) * 0.75
                + getEffectivePoints(
                    player, RpgStats.Stat.AGILITY
                ) * 0.4
                + getEffectivePoints(
                    player, RpgStats.Stat.INTELLIGENCE
                ) * 0.75;
    }

    public static double getArmorPenetration(Player player) {
        return Math.min(
                50.0,
                getEffectivePoints(
                        player, RpgStats.Stat.STRENGTH
                )
        );
    }

    public static double getAccuracyBonus(Player player) {
        return Math.min(
                50.0,
                getEffectivePoints(
                        player, RpgStats.Stat.PERCEPTION
                )
        );
    }

    public static double getCriticalChanceBonus(Player player) {
        return Math.min(
                50.0,
                getEffectivePoints(
                        player, RpgStats.Stat.PERCEPTION
                ) * 0.5
        );
    }

    public static double getCriticalPowerBonus(Player player) {
        return Math.min(
                200.0,
                getEffectivePoints(
                        player, RpgStats.Stat.AGILITY
                ) * 2.0
        );
    }

    public static double getEvasionBonus(
            Player player,
            boolean agilityMainClass
    ) {
        double agility = getEffectivePoints(
                player, RpgStats.Stat.AGILITY
        );

        return agility * (agilityMainClass ? 0.5 : 1.0);
    }

    public static double getHealthBonus(Player player) {
        return getEffectivePoints(
                player, RpgStats.Stat.ENDURANCE
        ) * 2.0;
    }

    public static double getArmorBonus(Player player) {
        return getEffectivePoints(
                player, RpgStats.Stat.ENDURANCE
        ) * 0.5;
    }

    public static double getManaCapacityBonus(Player player) {
        return getEffectivePoints(
                player, RpgStats.Stat.INTELLIGENCE
        ) * 1.8;
    }

    public static double getManaRegenerationBonus(Player player) {
        return getEffectivePoints(
                player, RpgStats.Stat.INTELLIGENCE
        ) * 0.06;
    }

    /**
     * Максимальне зниження шкоди бронею — 70%.
     * Параметр armorReductionPercent передається
     * іншою системою розрахунку броні.
     */
    public static double clampArmorReduction(
            double armorReductionPercent
    ) {
        return Math.max(
                0.0,
                Math.min(70.0, armorReductionPercent)
        );
    }

    /**
     * Базове зниження додаткової критичної шкоди — 25%.
     * Витривалість може додати до 15%.
     * Повний важкий комплект може додати ще 10%.
     */
    public static double getCriticalDamageReduction(
            Player player,
            boolean fullHeavyArmorSet
    ) {
        double enduranceShare =
                RpgPlayerData.getAllocationShare(
                        player,
                        RpgStats.Stat.ENDURANCE
                );

        double enduranceBonus = Math.max(
                0.0,
                Math.min(
                        15.0,
                        (enduranceShare - 0.20) * 30.0
                )
        );

        double armorBonus =
                fullHeavyArmorSet ? 10.0 : 0.0;

        return Math.min(
                50.0,
                25.0 + enduranceBonus + armorBonus
        );
    }
}
