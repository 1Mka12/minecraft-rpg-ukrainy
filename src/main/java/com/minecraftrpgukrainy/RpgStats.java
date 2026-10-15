package com.minecraftrpgukrainy;

import java.util.EnumMap;
import java.util.Map;

public class RpgStats {

    public static final int SKILL_UNLOCK_POINTS = 30;
    public static final double SOFT_CAP_SHARE = 0.60;
    public static final double ABOVE_SOFT_CAP_EFFICIENCY = 0.50;

    private final Map<Stat, Integer> points =
            new EnumMap<>(Stat.class);

    private int freePoints;

    public enum Stat {
        STRENGTH("Сила"),
        ENDURANCE("Витривалість"),
        PERCEPTION("Сприйняття"),
        AGILITY("Спритність"),
        INTELLIGENCE("Інтелект");

        private final String displayName;

        Stat(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Skill {
        BREACH(Stat.STRENGTH, "Пролом"),
        SECOND_WIND(Stat.ENDURANCE, "Друге дихання"),
        HUNTER_MARK(Stat.PERCEPTION, "Мітка мисливця"),
        DOUBLE_STRIKE(Stat.AGILITY, "Подвійний удар"),
        MANA_SHIELD(Stat.INTELLIGENCE, "Щит мани");

        private final Stat requiredStat;
        private final String displayName;

        Skill(Stat requiredStat, String displayName) {
            this.requiredStat = requiredStat;
            this.displayName = displayName;
        }

        public Stat getRequiredStat() {
            return requiredStat;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Archetype {
        SWORDSMAN,
        SLIME,
        SHIELD,
        DRAGON,
        GOBLIN,
        SPIDER_WOMAN,
        ASSASSIN,
        MONARCH,
        RETURNER,
        MAGE,
        LICH
    }

    public RpgStats() {
        for (Stat stat : Stat.values()) {
            points.put(stat, 0);
        }

        freePoints = 0;
    }

    public int getFreePoints() {
        return freePoints;
    }

    public void addFreePoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Кількість очок не може бути від'ємною."
            );
        }

        freePoints = Math.addExact(freePoints, amount);
    }

    public boolean allocatePoint(Stat stat) {
        if (stat == null || freePoints <= 0) {
            return false;
        }

        int current = points.get(stat);

        points.put(stat, Math.addExact(current, 1));
        freePoints--;

        return true;
    }

    public boolean removePoint(Stat stat) {
        if (stat == null || points.get(stat) <= 0) {
            return false;
        }

        points.put(stat, points.get(stat) - 1);
        freePoints = Math.addExact(freePoints, 1);

        return true;
    }

    public int getPoints(Stat stat) {
        return points.get(stat);
    }

    public int getTotalAllocatedPoints() {
        int total = 0;

        for (int value : points.values()) {
            total = Math.addExact(total, value);
        }

        return total;
    }

    public double getAllocationShare(Stat stat) {
        int total = getTotalAllocatedPoints();

        if (total == 0) {
            return 0.0;
        }

        return (double) getPoints(stat) / total;
    }

    public double getAllocationPercent(Stat stat) {
        return getAllocationShare(stat) * 100.0;
    }

    public double getEffectivePoints(Stat stat) {
        int allocated = getPoints(stat);
        int total = getTotalAllocatedPoints();

        if (total == 0) {
            return 0.0;
        }

        double softCap = total * SOFT_CAP_SHARE;

        if (allocated <= softCap) {
            return allocated;
        }

        return softCap
                + (allocated - softCap)
                * ABOVE_SOFT_CAP_EFFICIENCY;
    }

    public boolean isSkillUnlocked(Skill skill) {
        return skill != null
                && getPoints(skill.getRequiredStat())
                >= SKILL_UNLOCK_POINTS;
    }

    public Stat getMainAttackStat(Archetype archetype) {
        return switch (archetype) {
            case SWORDSMAN, SLIME, SHIELD, DRAGON ->
                    Stat.STRENGTH;

            case GOBLIN ->
                    Stat.PERCEPTION;

            case SPIDER_WOMAN, ASSASSIN, MONARCH, RETURNER ->
                    Stat.AGILITY;

            case MAGE, LICH ->
                    Stat.INTELLIGENCE;
        };
    }

    public double getAttackScalingPoints(Archetype archetype) {
        return getEffectivePoints(getMainAttackStat(archetype));
    }

    public double getCriticalPowerPoints(Archetype archetype) {
        if (archetype == Archetype.GOBLIN) {
            return getEffectivePoints(Stat.AGILITY);
        }

        return getEffectivePoints(Stat.PERCEPTION);
    }

    public double getEvasionPoints(Archetype archetype) {
        double agility = getEffectivePoints(Stat.AGILITY);

        return switch (archetype) {
            case SPIDER_WOMAN, ASSASSIN, MONARCH, RETURNER ->
                    agility * 0.5;

            default -> agility;
        };
    }

    public double getReactionPoints() {
        return getEffectivePoints(Stat.AGILITY);
    }

    public double getMaxManaBonus() {
        return getEffectivePoints(Stat.INTELLIGENCE) * 1.8;
    }

    public double getManaRegenerationBonus() {
        return getEffectivePoints(Stat.INTELLIGENCE) * 0.06;
    }

    public double getArmorDamageReduction(
            double armorReductionPercent) {

        return Math.max(
                0.0,
                Math.min(70.0, armorReductionPercent)
        );
    }

    public double getCriticalBonusReduction(
            boolean fullHeavyArmorSet) {

        double share = getAllocationShare(Stat.ENDURANCE);

        double enduranceBonus = Math.max(
                0.0,
                (share - 0.20) * 0.30
        );

        enduranceBonus = Math.min(0.15, enduranceBonus);

        double result = 0.25 + enduranceBonus;

        if (fullHeavyArmorSet) {
            result += 0.10;
        }

        return Math.min(0.50, result);
    }
  }
