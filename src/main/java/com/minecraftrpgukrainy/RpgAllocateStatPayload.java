
package com.minecraftrpgukrainy;

import net.minecraft.network.RegistryFriendlyByteBuf;
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
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            MinecraftRpgUkrainy.MOD_ID,
                            "stats_sync"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            RpgStatsSyncPayload
            > STREAM_CODEC = new StreamCodec<>() {

        @Override
        public RpgStatsSyncPayload decode(
                RegistryFriendlyByteBuf buf
        ) {
            return new RpgStatsSyncPayload(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt()
            );
        }

        @Override
        public void encode(
                RegistryFriendlyByteBuf buf,
                RpgStatsSyncPayload payload
        ) {
            buf.writeVarInt(payload.strength());
            buf.writeVarInt(payload.agility());
            buf.writeVarInt(payload.endurance());
            buf.writeVarInt(payload.perception());
            buf.writeVarInt(payload.intelligence());
            buf.writeVarInt(payload.freePoints());
            buf.writeVarInt(payload.level());
            buf.writeVarInt(payload.experience());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            RpgStatsSyncPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(
                () -> RpgClientStatsCache.apply(payload)
        );
    }
}
