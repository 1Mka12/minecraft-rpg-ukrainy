package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgCombatEvents {

    private RpgCombatEvents() {
    }

    /**
     * Оновлення ультимативної навички на сервері.
     * Викликається один раз на кожному серверному тику гравця.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        RpgGoblinUltimate.tick(player);
    }

    /**
     * Активація ультимативної навички гобліна.
     */
    public static boolean activateGoblinUltimate(ServerPlayer player) {
        return RpgGoblinUltimate.activate(player);
    }

    /**
     * Перевірка активності ультимативної навички.
     */
    public static boolean isGoblinUltimateActive(ServerPlayer player) {
        return RpgGoblinUltimate.isActive(player);
    }

    /**
     * Залишок часу дії навички в тіках.
     */
    public static int getGoblinUltimateRemainingTicks(
            ServerPlayer player) {
        return RpgGoblinUltimate.getRemainingTicks(player);
    }

    /**
     * Залишок перезаряджання в тіках.
     */
    public static int getGoblinUltimateCooldownTicks(
            ServerPlayer player) {
        return RpgGoblinUltimate.getCooldownTicks(player);
    }

    /**
     * Залишок перезаряджання в секундах.
     */
    public static int getGoblinUltimateCooldownSeconds(
            ServerPlayer player) {
        return RpgGoblinUltimate.getCooldownSeconds(player);
    }
}
