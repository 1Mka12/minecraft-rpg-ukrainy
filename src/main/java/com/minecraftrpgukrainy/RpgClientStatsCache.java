package com.minecraftrpgukrainy;

public final class RpgClientStatsCache {

    private static int strength;
    private static int agility;
    private static int endurance;
    private static int perception;
    private static int intelligence;

    private static int freePoints;
    private static int level = 1;
    private static int experience;

    private RpgClientStatsCache() {
    }

    public static void apply(RpgStatsSyncPayload payload) {
        strength = payload.strength();
        agility = payload.agility();
        endurance = payload.endurance();
        perception = payload.perception();
        intelligence = payload.intelligence();

        freePoints = payload.freePoints();
        level = payload.level();
        experience = payload.experience();
    }

    public static int getStrength() {
        return strength;
    }

    public static int getAgility() {
        return agility;
    }

    public static int getEndurance() {
        return endurance;
    }

    public static int getPerception() {
        return perception;
    }

    public static int getIntelligence() {
        return intelligence;
    }

    public static int getFreePoints() {
        return freePoints;
    }

    public static int getLevel() {
        return level;
    }

    public static int getExperience() {
        return experience;
    }
}
