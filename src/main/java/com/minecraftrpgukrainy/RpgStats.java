
package com.minecraftrpgukrainy;

import java.util.EnumMap;
import java.util.Map;

public class RpgStats {

    public static final int SKILL_UNLOCK_POINTS = 30;
    public static final double SOFT_CAP_SHARE = 0.60;
    public static final double ABOVE_SOFT_CAP_EFFICIENCY = 0.50;

    public static final double MANA_PER_INTELLIGENCE = 1.8;
    public static final double MANA_REGEN_PER_INTELLIGENCE = 0.06;

    public static final double MAX_ARMOR_DAMAGE_REDUCTION = 70.0;
    public static final double BASE_CRITICAL_REDUCTION = 25.0;
    public static final double MAX_ENDURANCE_CRITICAL_REDUCTION = 40.0;
    public static final double HEAVY_ARMOR_CRITICAL_BONUS = 10.0;
    public static final double MAX_CRITICAL_REDUCTION = 50.0;

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
        MANA_SHIELD(Stat.INTELLIGENCE, "Щит мани"),
        GOBLIN_ULTIMATE(
                Stat.PERCEPTION,
                "Інстинкт хижака"
        );

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

        points.put(
                stat,
                Math.addExact(points.get(stat), 1)
        );

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
        if (stat == null) {
            return 0;
        }

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

        if (stat == null || total == 0) {
            return 0.0;
        }

        return (double) getPoints(stat) / total;
    }

    public double getAllocationPercent(Stat stat) {
        return getAllocationShare(stat) * 100.0;
    }

    /**
     * Понад 60% усіх вкладених очок
     * кожне додаткове очко має половину ефективності.
     */
    public double getEffectivePoints(Stat stat) {
        if (stat == null) {
            return 0.0;
        }

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

    /**
     * «Інстинкт хижака»:
     * 30 очок Сприйняття.
     * Ранг S не потрібен.
     */
    public boolean isGoblinUltimateUnlocked() {
        return getPoints(Stat.PERCEPTION)
                >= SKILL_UNLOCK_POINTS;
    }

    public Stat getMainAttackStat(Archetype archetype) {
        if (archetype == null) {
            return Stat.STRENGTH;
        }

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

    /**
     * Ефективні очки головної характеристики атаки.
     */
    public double getAttackScalingPoints(Archetype archetype) {
        return getEffectivePoints(getMainAttackStat(archetype));
    }

    /**
     * Додаткова фізична атака від Сили.
     * Балансувальний коефіцієнт: 0.5 шкоди за очко.
     */
    public double getStrengthAttackBonus() {
        return getEffectivePoints(Stat.STRENGTH) * 0.5;
    }

    /**
     * Додаткова атака від Сприйняття.
     * Вона особливо важлива для Гобліна.
     */
    public double getPerceptionAttackBonus() {
        return getEffectivePoints(Stat.PERCEPTION) * 0.75;
    }

    /**
     * Додаткова атака від Спритності.
     */
    public double getAgilityAttackBonus() {
        return getEffectivePoints(Stat.AGILITY) * 0.4;
    }

    /**
     * Додаткова магічна атака від Інтелекту.
     */
    public double getIntelligenceAttackBonus() {
        return getEffectivePoints(Stat.INTELLIGENCE) * 0.75;
    }

    /**
     * Умовний бонус до пробиття броні у відсоткових пунктах.
     * Реальне зменшення броні цілі підключається в бойових подіях.
     */
    public double getArmorPenetrationPercent() {
        return Math.min(
                50.0,
                getEffectivePoints(Stat.STRENGTH) * 1.0
        );
    }

    /**
     * Бонус точності від Сприйняття.
     * Значення — відсоткові пункти.
     */
    public double getAccuracyBonusPercent() {
        return Math.min(
                50.0,
                getEffectivePoints(Stat.PERCEPTION) * 1.0
        );
    }

    /**
     * Шанс критичного удару від Сприйняття.
     * Значення — відсоткові пункти.
     */
    public double getCriticalChanceBonusPercent() {
        return Math.min(
                50.0,
                getEffectivePoints(Stat.PERCEPTION) * 0.5
        );
    }

    /**
     * Сила критичного удару залежить від Спритності.
     * Значення — додаткові відсоткові пункти шкоди.
     */
    public double getCriticalPowerBonusPercent() {
        return Math.min(
                200.0,
                getEffectivePoints(Stat.AGILITY) * 2.0
        );
    }

    /**
     * Зменшення ухилення для класів,
     * у яких головна характеристика — Спритність.
     */
    public double getEvasionPoints(Archetype archetype) {
        double agility = getEffectivePoints(Stat.AGILITY);

        if (archetype == null) {
            return agility;
        }

        return switch (archetype) {
            case SPIDER_WOMAN, ASSASSIN, MONARCH, RETURNER ->
                    agility * 0.5;

            default -> agility;
        };
    }

    /**
     * Реакція використовує повну ефективну Спритність.
     */
    public double getReactionPoints() {
        return getEffectivePoints(Stat.AGILITY);
    }

    /**
     * Бонус здоров'я від Витривалості.
     * Балансувальний коефіцієнт: 2 здоров'я за очко.
     */
    public double getMaxHealthBonus() {
        return getEffectivePoints(Stat.ENDURANCE) * 2.0;
    }

    /**
     * Бонус броні від Витривалості.
     * Балансувальний коефіцієнт: 0.5 одиниці за очко.
     */
    public double getArmorBonus() {
        return getEffectivePoints(Stat.ENDURANCE) * 0.5;
    }

    /**
     * Бонус до запасу мани.
     */
    public double getMaxManaBonus() {
        return getEffectivePoints(Stat.INTELLIGENCE)
                * MANA_PER_INTELLIGENCE;
    }

    /**
     * Бонус до відновлення мани.
     */
    public double getManaRegenerationBonus() {
        return getEffectivePoints(Stat.INTELLIGENCE)
                * MANA_REGEN_PER_INTELLIGENCE;
    }

    /**
     * Повертає обмежене зменшення отриманої шкоди.
     * Значення передається у відсотках.
     */
    public double getArmorDamageReduction(
            double armorReductionPercent) {

        return Math.max(
                0.0,
                Math.min(
                        MAX_ARMOR_DAMAGE_REDUCTION,
                        armorReductionPercent
                )
        );
    }

    /**
     * Базове загартування — 25%.
     * При 60% очок Витривалості — 37%.
     * Самостійний бонус Витривалості обмежений 40%.
     * Повний комплект важкої броні може додати 10%.
     */
    public double getCriticalBonusReduction(
            boolean fullHeavyArmorSet) {

        double share = getAllocationShare(Stat.ENDURANCE);

        double enduranceBonus = Math.max(
                0.0,
                (share - 0.20) * 30.0
        );

        enduranceBonus = Math.min(
                MAX_ENDURANCE_CRITICAL_REDUCTION
                        - BASE_CRITICAL_REDUCTION,
                enduranceBonus
        );

        double result =
                BASE_CRITICAL_REDUCTION + enduranceBonus;

        if (fullHeavyArmorSet) {
            result += HEAVY_ARMOR_CRITICAL_BONUS;
        }

        return Math.min(MAX_CRITICAL_REDUCTION, result);
    }
}
            
