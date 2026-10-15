package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = MinecraftRpgUkrainy.MOD_ID)
public final class RpgCombatEvents {

    private RpgCombatEvents() {
    }

    /**
     * Серверний тик гравця.
     * Оновлює тривалість і перезаряджання ультимативної навички гобліна.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        RpgGoblinUltimate.tick(player);
    }

    /**
     * Перевірка, чи може гравець активувати ультимативну навичку гобліна.
     */
    public static boolean activateGoblinUltimate(ServerPlayer player) {
        return RpgGoblinUltimate.activate(player);
    }

    /**
     * Перевірка активності навички.
     */
    public static boolean isGoblinUltimateActive(ServerPlayer player) {
        return RpgGoblinUltimate.isActive(player);
    }

    /**
     * Залишок перезаряджання в тіках.
     */
    public static int getGoblinUltimateCooldown(ServerPlayer player) {
        return RpgGoblinUltimate.getCooldownTicks(player);
    }

    /**
     * Залишок перезаряджання в секундах.
     */
    public static int getGoblinUltimateCooldownSeconds(
            ServerPlayer player) {
        int ticks = getGoblinUltimateCooldown(player);
        return (ticks + 19) / 20;
    }
}
