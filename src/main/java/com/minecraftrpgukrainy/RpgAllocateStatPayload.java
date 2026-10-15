
package com.minecraftrpgukrainy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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

    public static void handle(
            RpgAllocateStatPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            RpgStats.Stat[] stats = RpgStats.Stat.values();

            // Забороняємо від'ємні ID та ID поза межами масиву.
            if (payload.statId() < 0
                    || payload.statId() >= stats.length) {
                return;
            }

            RpgStats.Stat stat = stats[payload.statId()];

            if (stat == null) {
                return;
            }

            // Усі зміни виконуються тільки на сервері.
            if (payload.remove()) {
                // removePoint сам перевіряє наявність очка
                // у вибраній характеристиці.
                RpgPlayerData.removePoint(player, stat);
            } else {
                // allocatePoint перевіряє вільні очки
                // та допустимість збільшення характеристики.
                RpgPlayerData.allocatePoint(player, stat);
            }
        });
    }
}
