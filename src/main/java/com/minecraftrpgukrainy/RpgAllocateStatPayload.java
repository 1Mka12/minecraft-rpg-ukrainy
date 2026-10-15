
package com.minecraftrpgukrainy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.ByteBufCodecs;

public record RpgAllocateStatPayload(
        int statId,
        boolean remove
) implements CustomPacketPayload {

    public static final Type<RpgAllocateStatPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            MinecraftRpgUkrainy.MOD_ID,
                            "allocate_stat"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            RpgAllocateStatPayload
            > STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RpgAllocateStatPayload::statId,
                    ByteBufCodecs.BOOL,
                    RpgAllocateStatPayload::remove,
                    RpgAllocateStatPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
