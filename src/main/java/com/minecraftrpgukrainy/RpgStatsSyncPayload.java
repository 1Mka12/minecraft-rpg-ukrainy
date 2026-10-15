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
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    MinecraftRpgUkrainy.MOD_ID,
                    "stats_sync"
            ));

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            RpgStatsSyncPayload
            > STREAM_CODEC = new StreamCodec<>() {

        @Override
        public RpgStatsSyncPayload decode(
                RegistryFriendlyByteBuf buffer
        ) {
            int strength = buffer.readVarInt();
            int agility = buffer.readVarInt();
            int endurance = buffer.readVarInt();
            int perception = buffer.readVarInt();
            int intelligence = buffer.readVarInt();
            int freePoints = buffer.readVarInt();
            int level = buffer.readVarInt();
            int experience = buffer.readVarInt();

            return new RpgStatsSyncPayload(
                    strength,
                    agility,
                    endurance,
                    perception,
                    intelligence,
                    freePoints,
                    level,
                    experience
            );
        }

        @Override
        public void encode(
                RegistryFriendlyByteBuf buffer,
                RpgStatsSyncPayload payload
        ) {
            buffer.writeVarInt(payload.strength());
            buffer.writeVarInt(payload.agility());
            buffer.writeVarInt(payload.endurance());
            buffer.writeVarInt(payload.perception());
            buffer.writeVarInt(payload.intelligence());
            buffer.writeVarInt(payload.freePoints());
            buffer.writeVarInt(payload.level());
            buffer.writeVarInt(payload.experience());
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
        context.enqueueWork(() ->
                RpgClientStatsCache.apply(payload)
        );
    }
}
