
package com.minecraftrpgukrainy;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
            RegisterPayloadHandlersEvent event
    ) {
        var registrar = event.registrar("1");

        // Навички: клієнт -> сервер.
        registrar.playToServer(
                RpgSkillPayload.TYPE,
                RpgSkillPayload.STREAM_CODEC,
                RpgSkillPayload::handle
        );

        // Розподіл очок характеристик: клієнт -> сервер.
        registrar.playToServer(
                RpgAllocateStatPayload.TYPE,
                RpgAllocateStatPayload.STREAM_CODEC,
                RpgAllocateStatPayload::handle
        );
    }
}
