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
            RegisterPayloadHandlersEvent event) {

        event.registrar("1").playToServer(
                RpgSkillPayload.TYPE,
                RpgSkillPayload.STREAM_CODEC,
                RpgSkillPayload::handle
        );
    }
}
