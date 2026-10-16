
package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(
        modid = "minecraft_rpg_ukrainy",
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgExperienceEvents {

    private RpgExperienceEvents() {
    }

    @SubscribeEvent
    public static void onMonsterDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }

        if (!(event.getSource().getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        // Початкова винагорода за вбивство монстра.
        RpgPlayerLevel.addExperience(player, 8);

        // Оновлюємо рівень і досвід на екрані персонажа.
        RpgStatsSync.send(player);
    }
}
