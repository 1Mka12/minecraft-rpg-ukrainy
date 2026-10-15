package com.minecraftrpgukrainy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RpgStatsSyncPayload(
        int strength,
        int agility,
        int endurance,
        int perception,
        int intelligence,
        int freePoints,
        int level,
        int experience
) implements CustomPacketPayload {

    public static final Type<RpgStatsSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    "stats_sync"
            ));

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            RpgStatsSyncPayload
            > STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::strength,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::agility,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::endurance,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::perception,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::intelligence,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::freePoints,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::level,

                    ByteBufCodecs.VAR_INT,
                    RpgStatsSyncPayload::experience,

                    RpgStatsSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            RpgStatsSyncPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() ->
                RpgClientStatsCache.apply(payload)
        );
    }
}
