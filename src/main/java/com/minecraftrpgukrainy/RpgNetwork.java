
package com.minecraftrpgukrainy;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(
        modid = MinecraftRpgUkrainy.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD
)
public final class RpgNetwork {

    private RpgNetwork() {
    }

    @SubscribeEvent
    public static void registerPayloads(
            RegisterPayloadHandlersEvent event) {

        event.registrar("1").playToServer(
                RpgAllocateStatPayload.TYPE,
                RpgAllocateStatPayload.STREAM_CODEC,
                RpgNetwork::handleAllocateStat
        );
    }

    private static void handleAllocateStat(
            RpgAllocateStatPayload payload,
            IPayloadContext context) {

        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        int statId = payload.statId();

        RpgStats.Stat[] stats = RpgStats.Stat.values();

        if (statId < 0 || statId >= stats.length) {
            return;
        }

        RpgStats.Stat stat = stats[statId];

        if (payload.remove()) {
            RpgPlayerData.removePoint(player, stat);
        } else {
            RpgPlayerData.allocatePoint(player, stat);
        }
    }
}
