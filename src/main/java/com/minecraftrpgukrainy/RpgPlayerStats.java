
package com.minecraftrpgukrainy;

import net.minecraft.world.entity.player.Player;

public final class RpgPlayerStats {

    private RpgPlayerStats() {
    }

    public static int getStrength(Player player) {
        return RpgPlayerData.getPoints(
                player, RpgStats.Stat.STRENGTH
        );
    }

    public static int getEndurance(Player player) {
        return RpgPlayerData.getPoints(
                player, RpgStats.Stat.ENDURANCE
        );
    }

    public static int getPerception(Player player) {
        return RpgPlayerData.getPoints(
                player, RpgStats.Stat.PERCEPTION
        );
    }

    public static int getAgility(Player player) {
        return RpgPlayerData.getPoints(
                player, RpgStats.Stat.AGILITY
        );
    }

    public static int getIntelligence(Player player) {
        return RpgPlayerData.getPoints(
                player, RpgStats.Stat.INTELLIGENCE
        );
    }

    public static int getFreePoints(Player player) {
        return RpgPlayerData.getFreePoints(player);
    }

    public static int getTotalAllocatedPoints(Player player) {
        return RpgPlayerData.getTotalAllocatedPoints(player);
    }

    public static double getAllocationPercent(
            Player player, RpgStats.Stat stat) {
        return RpgPlayerData.getAllocationPercent(player, stat);
    }

    /**
     * Розраховує ефективні очки з урахуванням м'якого ліміту:
     * після 60% усіх вкладених очок ефективність надлишку — 50%.
     */
    public static double getEffectivePoints(
            Player player, RpgStats.Stat stat) {

        if (stat == null) {
            return 0.0;
        }

        int points = RpgPlayerData.getPoints(player, stat);
        int total = RpgPlayerData.getTotalAllocatedPoints(player);

        if (points <= 0 || total <= 0) {
            return 0.0;
        }

        double softCap = total * RpgStats.SOFT_CAP_SHARE;

        if (points <= softCap) {
            return points;
        }

        return softCap
                + (points - softCap)
                * RpgStats.ABOVE_SOFT_CAP_EFFICIENCY;
    }

    public static double getStrengthEffective(Player player) {
        return getEffectivePoints(player, RpgStats.Stat.STRENGTH);
    }

    public static double getEnduranceEffective(Player player) {
        return getEffectivePoints(player, RpgStats.Stat.ENDURANCE);
    }

    public static double getPerceptionEffective(Player player) {
        return getEffectivePoints(player, RpgStats.Stat.PERCEPTION);
    }

    public static double getAgilityEffective(Player player) {
        return getEffectivePoints(player, RpgStats.Stat.AGILITY);
    }

    public static double getIntelligenceEffective(Player player) {
        return getEffectivePoints(player, RpgStats.Stat.INTELLIGENCE);
    }

    public static boolean isSkillUnlocked(
            Player player, RpgStats.Skill skill) {
        return RpgPlayerData.isSkillUnlocked(player, skill);
    }

    public static double getMaxManaBonus(Player player) {
        return getIntelligenceEffective(player) * 1.8;
    }

    public static double getManaRegenerationBonus(Player player) {
        return getIntelligenceEffective(player) * 0.06;
    }

    public static double getStrengthAttackBonus(Player player) {
        return getStrengthEffective(player);
    }

    public static double getPerceptionAttackBonus(Player player) {
        return getPerceptionEffective(player);
    }

    public static double getAgilityAttackBonus(Player player) {
        return getAgilityEffective(player);
    }

    public static double getIntelligenceAttackBonus(Player player) {
        return getIntelligenceEffective(player);
    }

    public static double getArmorDamageReduction(
            double armorReduction) {
        return Math.max(0.0, Math.min(0.70, armorReduction));
    }

    public static double getEvasionPoints(Player player) {
        return getAgilityEffective(player);
    }

    public static double getReactionPoints(Player player) {
        return getAgilityEffective(player);
    }

    public static double getCriticalPowerPoints(Player player) {
        return getPerceptionEffective(player);
    }
}
