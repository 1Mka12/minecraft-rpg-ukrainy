
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
        if (payload == null) {
            return;
        }

        strength = Math.max(0, payload.strength());
        agility = Math.max(0, payload.agility());
        endurance = Math.max(0, payload.endurance());
        perception = Math.max(0, payload.perception());
        intelligence = Math.max(0, payload.intelligence());

        freePoints = Math.max(0, payload.freePoints());
        level = Math.max(1, payload.level());
        experience = Math.max(0, payload.experience());
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
        return Math.max(1, level);
    }

    public static int getExperience() {
        return Math.max(0, experience);
    }
}
