
package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgCombatEvents {

    private RpgCombatEvents() {
    }

    /**
     * Оновлення ультимативної навички гобліна.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        RpgGoblinUltimate.tick(player);
    }

    /**
     * Сила додає фізичну шкоду.
     * Інтелект додає шкоду магічним атакам.
     */
    @SubscribeEvent
    public static void onIncomingDamage(
            LivingIncomingDamageEvent event
    ) {
        if (!(event.getSource().getEntity()
                instanceof ServerPlayer attacker)) {
            return;
        }

        if (isMagicDamage(event)) {
            double intelligence = getEffectivePoints(
                    attacker,
                    RpgStats.Stat.INTELLIGENCE
            );

            double magicBonus = intelligence * 0.75;

            if (magicBonus > 0.0) {
                event.setAmount(
                        event.getAmount() + (float) magicBonus
                );
            }

            return;
        }

        double strength = getEffectivePoints(
                attacker,
                RpgStats.Stat.STRENGTH
        );

        double physicalBonus = strength * 0.5;

        if (physicalBonus > 0.0) {
            event.setAmount(
                    event.getAmount() + (float) physicalBonus
            );
        }
    }

    private static boolean isMagicDamage(
            LivingIncomingDamageEvent event
    ) {
        return event.getSource().is(DamageTypes.MAGIC)
                || event.getSource().is(DamageTypes.INDIRECT_MAGIC);
    }

    /**
     * Розрахунок ефективних очок з урахуванням обмеження 60% / 50%.
     */
    private static double getEffectivePoints(
            ServerPlayer player,
            RpgStats.Stat stat
    ) {
        int allocated = RpgPlayerData.getPoints(player, stat);

        int total = RpgPlayerData.getTotalAllocatedPoints(player);

        if (allocated <= 0 || total <= 0) {
            return 0.0;
        }

        double softCap = total * RpgStats.SOFT_CAP_SHARE;

        if (allocated <= softCap) {
            return allocated;
        }

        return softCap
                + (allocated - softCap)
                * RpgStats.ABOVE_SOFT_CAP_EFFICIENCY;
    }

    public static boolean activateGoblinUltimate(
            ServerPlayer player
    ) {
        return RpgGoblinUltimate.activate(player);
    }

    public static boolean isGoblinUltimateActive(
            ServerPlayer player
    ) {
        return RpgGoblinUltimate.isActive(player);
    }

    public static int getGoblinUltimateRemainingTicks(
            ServerPlayer player
    ) {
        return RpgGoblinUltimate.getRemainingTicks(player);
    }

    public static int getGoblinUltimateCooldownTicks(
            ServerPlayer player
    ) {
        return RpgGoblinUltimate.getCooldownTicks(player);
    }

    public static int getGoblinUltimateCooldownSeconds(
            ServerPlayer player
    ) {
        return RpgGoblinUltimate.getCooldownSeconds(player);
    }
}
