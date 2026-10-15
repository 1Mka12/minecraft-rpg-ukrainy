
package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class RpgStatsSync {

    private RpgStatsSync() {
    }

    public static void send(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        RpgStatsSyncPayload payload = new RpgStatsSyncPayload(
                RpgPlayerData.getPoints(
                        player, RpgStats.Stat.STRENGTH),
                RpgPlayerData.getPoints(
                        player, RpgStats.Stat.AGILITY),
                RpgPlayerData.getPoints(
                        player, RpgStats.Stat.ENDURANCE),
                RpgPlayerData.getPoints(
                        player, RpgStats.Stat.PERCEPTION),
                RpgPlayerData.getPoints(
                        player, RpgStats.Stat.INTELLIGENCE),
                RpgPlayerData.getFreePoints(player),
                RpgPlayerLevel.getLevel(player),
                RpgPlayerLevel.getExperience(player)
        );

        PacketDistributor.sendToPlayer(player, payload);
    }

    @SubscribeEvent
    public static void onLogin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            send(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Оновлюємо кеш двічі на секунду.
        if (player.tickCount % 10 == 0) {
            send(player);
        }
    }
}
