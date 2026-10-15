
package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgCombatEvents {

    private RpgCombatEvents() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (victim instanceof ServerPlayer) {
            return;
        }

        int experience = getExperienceReward(victim);

        if (experience > 0) {
            RpgPlayerLevel.addExperience(player, experience);
        }
    }

    private static int getExperienceReward(LivingEntity victim) {
        String entityId = victim.getType()
                .builtInRegistryHolder()
                .key()
                .location()
                .getPath();

        return switch (entityId) {
            case "ender_dragon" -> 500;
            case "wither" -> 300;
            case "warden" -> 200;
            case "ravager", "elder_guardian" -> 100;
            case "witch", "evoker", "piglin_brute" -> 50;
            case "spider", "cave_spider", "creeper",
                 "enderman", "blaze", "slime" -> 20;
            case "zombie", "skeleton", "stray",
                 "drowned", "husk", "pillager" -> 15;
            case "cow", "pig", "sheep", "chicken",
                 "rabbit" -> 5;
            default -> 10;
        };
    }
}
